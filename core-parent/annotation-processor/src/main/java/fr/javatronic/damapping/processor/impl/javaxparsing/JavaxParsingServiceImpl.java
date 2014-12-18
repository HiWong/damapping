/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.processor.impl.javaxparsing.model.JavaxDAMethod;
import fr.javatronic.damapping.processor.impl.javaxparsing.model.JavaxDASourceClass;
import fr.javatronic.damapping.processor.model.DAInterface;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.processor.model.function.ToGuavaFunctionOrMapperMethod;
import fr.javatronic.damapping.processor.model.impl.DAInterfaceImpl;
import fr.javatronic.damapping.processor.model.impl.DAMethodImpl;
import fr.javatronic.damapping.processor.model.impl.DASourceClassImpl;
import fr.javatronic.damapping.util.Function;
import fr.javatronic.damapping.util.Predicates;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor6;

import static fr.javatronic.damapping.processor.model.impl.DAMethodImpl.makeGuavaFunctionApplyMethod;
import static fr.javatronic.damapping.processor.model.impl.DAMethodImpl.makeMapperMethod;
import static fr.javatronic.damapping.util.FluentIterable.from;
import static fr.javatronic.damapping.util.Preconditions.checkNotNull;
import static fr.javatronic.damapping.util.Predicates.notNull;

/**
 * JavaxParsingService -
 *
 * @author Sébastien Lesaint
 */
public class JavaxParsingServiceImpl implements JavaxParsingService {
  @Nonnull
  private final ProcessingEnvironmentWrapper processingEnv;

  public JavaxParsingServiceImpl(@Nonnull ProcessingEnvironmentWrapper processingEnv) {
    this.processingEnv = checkNotNull(processingEnv);
  }

  @Nonnull
  @Override
  public ParsingResult parse(@Nonnull TypeElement classElement,
                             @Nullable Collection<DAType> generatedTypes) throws IOException {
    ReferenceScanResult scanResult = new ReferencesScanner(processingEnv, generatedTypes).scan(classElement);
    if (scanResult.hasUnresolved()) {
      return ParsingResult.later(classElement, null, scanResult.getUnresolved());
    }

    DAType type = null;
    try {
      JavaxExtractorImpl javaxExtractor = new JavaxExtractorImpl(processingEnv, scanResult);
      type = javaxExtractor.extractType(classElement.asType());
      DASourceClass daSourceClass = parseImpl(classElement, type, javaxExtractor);

      return ParsingResult.ok(classElement, daSourceClass);
    } catch (Exception e) {
      processingEnv.printMessage(Mapper.class, classElement, e);
      return ParsingResult.failed(classElement, type);
    }
  }

  @Nonnull
  private DASourceClass parseImpl(TypeElement classElement, DAType type, JavaxExtractor javaxExtractor) {
    DASourceClassImpl.Builder<?> builder;
    if (classElement.getKind() == ElementKind.ENUM) {
      builder = DASourceClassImpl.enumBuilder(type, javaxExtractor.extractEnumValues(classElement));
    }
    else if (classElement.getKind() == ElementKind.CLASS) {
      builder = DASourceClassImpl.classbuilder(type);
    }
    else {
      throw new IllegalArgumentException("Unsupported Kind of TypeElement, must be either CLASS or ENUM");
    }

    builder.withAnnotations(javaxExtractor.extractDAAnnotations(classElement));

    builder.withModifiers(
        from(classElement.getModifiers()).transform(javaxExtractor.toDAModifier()).toSet()
    );

    // retrieve interfaces implemented (directly and if any) by the class with @Mapper (+ their generics)
    // chercher si l'une d'elles est Function (Guava)
    List<DAInterface> interfaces = retrieveInterfaces(classElement, javaxExtractor);
    builder.withInterfaces(interfaces);

    // pour le moment, on ne traite pas les classes abstraites implémentées par la class @Mapper ni les interfaces
    // implémentées indirectement

    builder.withMethods(
        from(retrieveMethods(classElement, javaxExtractor))
            .transform(new JavaxToGuavaFunctionOrMapperMethod(interfaces))
            .toList()
    );

    return new JavaxDASourceClass(builder.build(), classElement);
  }

  @Nonnull
  private Iterable<JavaxDAMethod> retrieveMethods(final TypeElement classElement, final JavaxExtractor javaxExtractor) {
    if (classElement.getEnclosedElements() == null) {
      return Collections.emptyList();
    }

    return from(classElement.getEnclosedElements())
        // methods are ExecutableElement
        .filter(Predicates.instanceOf(ExecutableElement.class))
         // transform
        .transform(new Function<Element, JavaxDAMethod>() {
          @Nullable
          @Override
          public JavaxDAMethod apply(@Nullable Element o) {
            if (o == null) {
              return null;
            }

            ExecutableElement methodElement = (ExecutableElement) o;
            DAMethodImpl.Builder builder = daMethodBuilder(methodElement);
            DAMethodImpl.Builder res = builder
                .withAnnotations(javaxExtractor.extractDAAnnotations(methodElement))
                .withModifiers(javaxExtractor.extractModifiers(methodElement))
                .withParameters(javaxExtractor.extractParameters(methodElement));
            if (o.getKind() == ElementKind.CONSTRUCTOR) {
              res.withName(DANameFactory.from(uncapitalize(classElement.getSimpleName().toString())));
              res.withReturnType(javaxExtractor.extractType(classElement.asType()));
            }
            else {
              res.withName(JavaxDANameFactory.from(o.getSimpleName()));
              res.withReturnType(javaxExtractor.extractReturnType(methodElement));
            }
            return new JavaxDAMethod(res.build(), methodElement);
          }
        }
        )
        .filter(notNull());
  }

  private static class JavaxToGuavaFunctionOrMapperMethod extends ToGuavaFunctionOrMapperMethod<JavaxDAMethod> {

    public JavaxToGuavaFunctionOrMapperMethod(List<DAInterface> interfaces) {
      super(interfaces);
    }

    @Override
    @Nonnull
    protected DAMethod toMapperMethod(@Nonnull JavaxDAMethod daMethod) {
      return new JavaxDAMethod(makeMapperMethod(daMethod), daMethod.getMethodElement());
    }

    @Override
    @Nonnull
    protected DAMethod toGuavaFunction(@Nonnull JavaxDAMethod daMethod) {
      return new JavaxDAMethod(makeGuavaFunctionApplyMethod(daMethod), daMethod.getMethodElement());
    }
  }

  public static String uncapitalize(String str) {
    if (str == null || str.length() == 0) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str.length());
    sb.append(Character.toLowerCase(str.charAt(0)));
    sb.append(str.substring(1));
    return sb.toString();
  }

  private DAMethodImpl.Builder daMethodBuilder(ExecutableElement element) {
    if (element.getKind() == ElementKind.METHOD) {
      return DAMethodImpl.methodBuilder();
    }
    if (element.getKind() == ElementKind.CONSTRUCTOR) {
      return DAMethodImpl.constructorBuilder();
    }
    throw new IllegalArgumentException(
        String.format(
            "Kind %s of element %s is not supported to build a DAMethod from", element.getKind(),
            element
        )
    );
  }

  private List<DAInterface> retrieveInterfaces(final TypeElement classElement,
                                               final JavaxExtractor javaxExtractor) {
    List<? extends TypeMirror> interfaces = classElement.getInterfaces();
    if (interfaces == null) {
      return Collections.emptyList();
    }

    return from(interfaces)
        .transform(new TypeMirrorToDAInterface(javaxExtractor))
        .filter(notNull())
        .toList();
  }

  private static class TypeMirrorToDAInterface implements Function<TypeMirror, DAInterface> {
    private final SimpleTypeVisitor6<DAInterface, Void> daInterfaceExtracter;

    public TypeMirrorToDAInterface(final JavaxExtractor javaxExtractor) {
      this.daInterfaceExtracter = new SimpleTypeVisitor6<DAInterface, Void>() {

        // TOIMPROVE : le filtrage des interfaces de la classe annotée avec @Mapper sur DeclaredType est-il pertinent ?
        @Override
        public DAInterface visitDeclared(DeclaredType declaredType, Void aVoid) {
          return new DAInterfaceImpl(javaxExtractor.extractType(declaredType));
        }
      };
    }

    @Nullable
    @Override
    public DAInterface apply(@Nullable TypeMirror o) {
      return o.accept(daInterfaceExtracter, null);
    }
  }
}
