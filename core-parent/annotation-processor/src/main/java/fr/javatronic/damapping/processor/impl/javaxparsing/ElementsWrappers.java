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

import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.factory.DANameFactory;
import fr.javatronic.damapping.util.Maps;
import fr.javatronic.damapping.util.Optional;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Pair;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * ElementsWrappers -
 *
 * TODO remove System.err and System.out calls from ElementsWrappers
 *
 * @author Sébastien Lesaint
 */
public final class ElementsWrappers {

  private static final QualifiedIdentifierVisitor IMPORT_QUALIFIED_NAME_VISITOR = new QualifiedIdentifierVisitor();
  // FIXME this pattern does not support with blank characters _inside_ the qualified name such as the following

  private ElementsWrappers() {
    // prevents instantiation
  }

  @Nonnull
  public static ElementsWrapper from(@Nonnull Elements elements) {
    if (elements instanceof JavacElements) {
      return new JavacElementsWrapper(elements);
    }

    return new DefaultElementsWrapper(elements);
  }

  private static void addAllImplicitImports(Elements elements, Element e, Map<Name, String> elementBySimpleName) {
    // add class from specified element's package which are implicitly imported
    // add it first so that if it exists any explicit import with a simpleName of a class in the current package,
    // it will overwrite the one from the current package in the map
    PackageElement currentPackage = elements.getPackageOf(e);
    // retrieve PackageElement of current package using Elements#getPackageElement(CharSequence) to get an up
    // -to-date version
    // of the package (ie. contains the generated classes as enclosed elements)
    // PackageElement returned by Elements#getPackage(Element) returns the PackageElement created for the current
    // package at the same time the Element was created (which is before the classes were generated by DAMapping)
    PackageElement upToDateCurrentPackage = elements.getPackageElement(currentPackage.getQualifiedName());
    addAllFromPackage(upToDateCurrentPackage, elementBySimpleName);
  }

  private static void addAddFromQualifiedImportName(Elements elements, Map<Name, String> elementBySimpleName,
                                                    String qualifiedImport) {
    if (qualifiedImport.endsWith("*")) {
      String packageQualifiedName = qualifiedImport.substring(0, qualifiedImport.indexOf("*") - 1);
      PackageElement packageElement = elements.getPackageElement(packageQualifiedName);
      if (packageElement == null) {
        System.err.println("Failed to retrieve PackageElement for qualifiedName " + packageQualifiedName);
      }
      else {
        addAllFromPackage(packageElement, elementBySimpleName);
      }
    }
    else {
      DAName simpleName = DANameFactory.simpleFromQualified(qualifiedImport);
      elementBySimpleName.put(elements.getName(simpleName), qualifiedImport);
    }
  }

  private static void addAllFromPackage(@Nullable PackageElement currentPackage,
                                        @Nonnull Map<Name, String> elementBySimpleName) {
    if (currentPackage == null || currentPackage.getEnclosedElements().isEmpty()) {
      return;
    }

    for (TypeElement typeElement : ElementFilter.typesIn(currentPackage.getEnclosedElements())) {
      elementBySimpleName.put(typeElement.getSimpleName(), typeElement.getQualifiedName().toString());
    }
  }

  private static class ElementWriter {
    private final Elements elements;
    private final Element e;

    private ElementWriter(Elements elements, Element e) {
      this.elements = elements;
      this.e = e;
    }

    @Override
    public String toString() {
      StringWriter stringWriter = new StringWriter();
      elements.printElements(stringWriter, e);
      return stringWriter.toString();
    }
  }

  private static abstract class BaseElementsWrapper<T extends Elements> implements ElementsWrapper {
    @Nonnull
    protected final T elements;

    protected BaseElementsWrapper(@Nonnull T elements) {
      this.elements = elements;
    }

    public PackageElement getPackageElement(CharSequence name) {
      return elements.getPackageElement(name);
    }

    public TypeElement getTypeElement(CharSequence name) {
      return elements.getTypeElement(name);
    }

    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(
        AnnotationMirror a) {
      return elements.getElementValuesWithDefaults(a);
    }

    public String getDocComment(Element e) {
      return elements.getDocComment(e);
    }

    public boolean isDeprecated(Element e) {
      return elements.isDeprecated(e);
    }

    public Name getBinaryName(TypeElement type) {
      return elements.getBinaryName(type);
    }

    public PackageElement getPackageOf(Element type) {
      return elements.getPackageOf(type);
    }

    public List<? extends Element> getAllMembers(TypeElement type) {
      return elements.getAllMembers(type);
    }

    public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
      return elements.getAllAnnotationMirrors(e);
    }

    public boolean hides(Element hider, Element hidden) {
      return elements.hides(hider, hidden);
    }

    public boolean overrides(ExecutableElement overrider,
                             ExecutableElement overridden,
                             TypeElement type) {
      return elements.overrides(overrider, overridden, type);
    }

    public String getConstantExpression(Object value) {
      return elements.getConstantExpression(value);
    }

    public void printElements(Writer w, Element... elements) {
      this.elements.printElements(w, elements);
    }

    public Name getName(CharSequence cs) {
      return elements.getName(cs);
    }

  }

  /**
   * This a {@link com.sun.source.tree.TreeVisitor} implemented extending {@link SimpleTreeVisitor} to extract the
   * qualified identifier as a String from a {@link MemberSelectTree}.
   * <p/>
   * <p>
   * A qualified identifier is represented as a tree of {@link MemberSelectTree}s that ends with a {@link
   * IdentifierTree}.
   * </p>
   * <p/>
   * <p>
   * E.g. "foo.bar.Acme":
   * <p/>
   * <pre>
   *   [MemberSelectTree: expression=  identifier=[IdentifierTree: name="Acme"]]
   *                                |
   *                                --> [MemberSelectTree: expression= identifier=[IdentifierTree: name="bar"]]
   *                                                                  |
   *                                                                  --> [MemberSelectTree: expression=null
   *                                                                  identifier=[IdentifierTree: name="foo"]]
   * </pre>
   * </p>
   */
  private static class QualifiedIdentifierVisitor extends SimpleTreeVisitor<StringBuffer, StringBuffer> {
    @Override
    public StringBuffer visitMemberSelect(MemberSelectTree memberSelectTree, StringBuffer buffer) {
      memberSelectTree.getExpression().accept(this, buffer);
      buffer.append(".").append(memberSelectTree.getIdentifier().toString());
      return buffer;
    }

    @Override
    public StringBuffer visitIdentifier(IdentifierTree identifierTree, StringBuffer buffer) {
      buffer.append(identifierTree.getName().toString());
      return buffer;
    }
  }

  /**
   * Simple implements of the ElementImports interface which uses a {@link Map} of qualifiedName as {@link String}
   * by simple names as {@link Name}.
   */
  private static class ElementImportsImpl implements ElementImports {
    @Nonnull
    private final Map<Name, String> elementBySimpleName;
    @Nonnull
    private final Elements elements;

    private ElementImportsImpl(@Nonnull Map<Name, String> elementBySimpleName,
                               @Nonnull Elements elements) {
      this.elementBySimpleName = checkNotNull(elementBySimpleName);
      this.elements = checkNotNull(elements);
    }

    @Nonnull
    @Override
    public Optional<String> findBySimpleName(@Nullable CharSequence simpleName) {
      return Optional.fromNullable(elementBySimpleName.get(elements.getName(simpleName)));
    }
  }

  /**
   * {@link ElementsWrapper} for Javac.
   * <p>
   * Tries to retrieve imports through Javac's JCCompilationUnit class. If it can't (ie. the Element does not compile),
   * defaults to parsing the import statements in the source file directly.
   * </p>
   * <p>
   * There is no compilation between annotation processing rounds
   *  <ul>
   *    <li>file which does not compile when annotation processing starts will never have a compilation unit</li>
   *    <li>however, package will be updated with generated mappers (if they compile) as enclosed elements
   *     from the round that follows the one during which they were generated</li>
   *  </ul>
   * </p>
   */
  private static class JavacElementsWrapper extends BaseElementsWrapper<JavacElements> {

    public JavacElementsWrapper(Elements elements) {
      super((JavacElements) elements);
    }

    @Nonnull
    @Override
    public ElementImports findImports(@Nonnull Element e) throws IOException {

      Map<Name, String> elementBySimpleName = Maps.newHashMap();

      addAllImplicitImports(elements, e, elementBySimpleName);

      Pair<JCTree, JCTree.JCCompilationUnit> treeAndTopLevel = elements.getTreeAndTopLevel(e, null, null);
      if (treeAndTopLevel == null || treeAndTopLevel.snd == null) {
        System.err.println("Failed to retrieve compilationUnit for " + new ElementWriter(elements, e));

        addAllFromSourceFile(elementBySimpleName, e);
      }
      else {
        addAllFromJCImports(elementBySimpleName, treeAndTopLevel.snd, e);
      }

      return new ElementImportsImpl(elementBySimpleName, elements);
    }

    private void addAllFromJCImports(Map<Name, String> elementBySimpleName,
                                     JCTree.JCCompilationUnit compilationUnit,
                                     Element element) {
      com.sun.tools.javac.util.List<JCTree.JCImport> imports = compilationUnit.getImports();
      if (imports == null || imports.isEmpty()) {
        System.err.println("No imports for " + new ElementWriter(elements, element));
        return;
      }

      for (JCTree.JCImport jcImport : imports) {
        String qualifiedImport = IMPORT_QUALIFIED_NAME_VISITOR.visit(
            jcImport.getQualifiedIdentifier(), new StringBuffer()
        ).toString();

        if (qualifiedImport.isEmpty()) {
          System.err.println("Failed to retrieve String qualified value for import " + jcImport);
          continue;
        }

        addAddFromQualifiedImportName(elements, elementBySimpleName, qualifiedImport);
      }
    }

    private void addAllFromSourceFile(Map<Name, String> elementBySimpleName, Element element) throws IOException {
      // so:
      // => we need to parse the source file of dedicated classes which does not compile to get the imports
      // written in source
      // when the source file does not compile, even if a previous round of annotation processing make

      // ((Symbol.ClassSymbol) e).sourcefile.getCharContent(true) to retrieve file content
      //  - alternative: ((Symbol.ClassSymbol) e).sourcefile.openReader(true)
      CharSequence sourceContent = ((Symbol.ClassSymbol) element).sourcefile.getCharContent(true);

      // FIXME reimplement parsing import statement with a reader to avoid reading all the file and loading it
      // all in memory

      for (String qualifiedImport : ImportStatementParserImpl.INSTANCE.parse(sourceContent)) {
        if (qualifiedImport == null || qualifiedImport.isEmpty()) {
          continue;
        }

        addAddFromQualifiedImportName(elements, elementBySimpleName, qualifiedImport);
      }
    }
  }

  private static interface ImportStatementParser {

    @Nonnull
    Iterable<String> parse(@Nullable CharSequence charSequence);

  }

  private static enum ImportStatementParserImpl implements ImportStatementParser {
    INSTANCE;

    // TODO improve import statement parsing as they can have any number of blank characters before and after each identifier (even line returns)
    // "         import    javax.   lang.   model.   type  .    DeclaredType    ;     "
    private static final Pattern IMPORT_STMT_PATTERN = Pattern.compile("^\\s*import\\s*([\\w\\.]+)\\s*;s*$",
        Pattern.MULTILINE
    );

    @Nonnull
    @Override
    public Iterable<String> parse(@Nullable CharSequence charSequence) {
      if (charSequence == null  || charSequence.length() == 0) {
        return Collections.emptyList();
      }

      final Matcher matcher = IMPORT_STMT_PATTERN.matcher(charSequence);

      return new Iterable<String>() {
        @Override
        public Iterator<String> iterator() {
          return new Iterator<String>() {
            @Override
            public boolean hasNext() {
              return matcher.find();
            }

            @Override
            public String next() {
              return matcher.group(1);
            }

            @Override
            public void remove() {
              throw new UnsupportedOperationException("remove is not supported by this implementation of Iterator");
            }
          };
        }
      };
    }
  }

  /**
   * This is an implementation of {@link ElementsWrapper} interface which provides a {@code findImports(Element)] method
   * which only supports implicite imports.
   */
  private static class DefaultElementsWrapper extends BaseElementsWrapper<Elements> {

    public DefaultElementsWrapper(Elements elements) {
      super(elements);
    }

    @Nonnull
    @Override
    public ElementImports findImports(@Nonnull Element e) {

      Map<Name, String> elementBySimpleName = Maps.newHashMap();

      addAllImplicitImports(elements, e, elementBySimpleName);

      return new ElementImportsImpl(elementBySimpleName, elements);
    }

  }
}
