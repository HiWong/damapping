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
package fr.javatronic.damapping.processor.sourcegenerator;

import fr.javatronic.damapping.processor.model.DAImport;
import fr.javatronic.damapping.processor.model.DAName;
import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.InstantiationType;
import fr.javatronic.damapping.processor.sourcegenerator.imports.MapperFactoryImplImportsModelVisitor;
import fr.javatronic.damapping.processor.sourcegenerator.imports.MapperFactoryInterfaceImportsModelVisitor;
import fr.javatronic.damapping.processor.sourcegenerator.imports.MapperImplImportsModelVisitor;
import fr.javatronic.damapping.processor.sourcegenerator.imports.MapperInterfaceImportsModelVisitor;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Sets;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.processor.model.factory.DATypeFactory.declared;
import static fr.javatronic.damapping.processor.sourcegenerator.GenerationContext.MAPPER_FACTORY_IMPL_KEY;
import static fr.javatronic.damapping.processor.sourcegenerator.GenerationContext.MAPPER_FACTORY_INTERFACE_KEY;
import static fr.javatronic.damapping.processor.sourcegenerator.GenerationContext.MAPPER_IMPL_KEY;
import static fr.javatronic.damapping.processor.sourcegenerator.GenerationContext.MAPPER_INTERFACE_KEY;

/**
 * GenerationContextComputerImpl -
 *
 * @author Sébastien Lesaint
 */
public class GenerationContextComputerImpl implements GenerationContextComputer {
  private static final Set<InstantiationType> MAPPER_FACTORY_CLASS_INTANTIATIONTYPES =
      Sets.of(InstantiationType.CONSTRUCTOR, InstantiationType.SINGLETON_ENUM);
  private static final Set<InstantiationType> MAPPER_FACTORY_INTERFACE_INTANTIATIONTYPES =
      Sets.of(InstantiationType.CONSTRUCTOR_FACTORY, InstantiationType.STATIC_FACTORY);

  @Override
  @Nonnull
  public GenerationContext compute(@Nonnull DASourceClass sourceClass) {
    return new DefaultGenerationContext(
        sourceClass, buildDescriptors(sourceClass)
    );
  }

  private List<DefaultGenerationContext.PartialDescriptor> buildDescriptors(DASourceClass sourceClass) {
    List<DefaultGenerationContext.PartialDescriptor> res = Lists.of();

    res.add(
        new DefaultGenerationContext.PartialDescriptor(
            MAPPER_INTERFACE_KEY,
            declared(sourceClass.getType().getQualifiedName() + "Mapper"),
            getMapperInterfaceImports(sourceClass),
            new SourceGeneratorFactory() {
              @Override
              public SourceGenerator instance(GeneratedFileDescriptor descriptor) {
                return new MapperSourceGenerator(descriptor);
              }
            }
        )
    );
    if (shouldGenerateMapperFactoryInterface(sourceClass)) {
      res.add(
          new DefaultGenerationContext.PartialDescriptor(
              MAPPER_FACTORY_INTERFACE_KEY,
              declared(sourceClass.getType().getQualifiedName() + "MapperFactory"),
              getMapperFactoryInterfaceImports(sourceClass),
              new SourceGeneratorFactory() {
                @Override
                public SourceGenerator instance(GeneratedFileDescriptor descriptor) {
                  return new MapperFactoryInterfaceSourceGenerator(descriptor);
                }
              }
          )
      );
    }
    if (shouldGenerateMapperImpl(sourceClass)) {
      res.add(
          new DefaultGenerationContext.PartialDescriptor(
              MAPPER_IMPL_KEY,
              declared(sourceClass.getType().getQualifiedName() + "MapperImpl"),
              getMapperImplImports(sourceClass),
              new SourceGeneratorFactory() {
                @Override
                public SourceGenerator instance(GeneratedFileDescriptor descriptor) {
                  return new MapperImplSourceGenerator(descriptor);
                }
              }
          )
      );
    }
    if (shouldGenerateMapperFactoryImpl(sourceClass)) {
      res.add(
          new DefaultGenerationContext.PartialDescriptor(
              MAPPER_FACTORY_IMPL_KEY,
              declared(sourceClass.getType().getQualifiedName() + "MapperFactoryImpl"),
              getMapperFactoryImplImports(sourceClass),
              new SourceGeneratorFactory() {
                @Override
                public SourceGenerator instance(GeneratedFileDescriptor descriptor) {
                  return new MapperFactoryImplSourceGenerator(descriptor);
                }
              }
          )
      );
    }
    return res;
  }

  private static List<DAImport> getMapperInterfaceImports(DASourceClass sourceClass) {
    MapperInterfaceImportsModelVisitor visitor = new MapperInterfaceImportsModelVisitor();
    sourceClass.accept(visitor);
    return visitor.getImports(sourceClass.getPackageName().getName());
  }

  private static List<DAImport> getMapperFactoryInterfaceImports(DASourceClass sourceClass) {
    MapperFactoryInterfaceImportsModelVisitor visitor = new MapperFactoryInterfaceImportsModelVisitor();
    sourceClass.accept(visitor);
    return visitor.getImports(sourceClass.getPackageName().getName());
  }

  private static List<DAImport> getMapperImplImports(DASourceClass sourceClass) {
    MapperImplImportsModelVisitor visitor = new MapperImplImportsModelVisitor();
    sourceClass.accept(visitor);
    return visitor.getImports(sourceClass.getPackageName().getName());
  }

  private static List<DAImport> getMapperFactoryImplImports(DASourceClass sourceClass) {
    MapperFactoryImplImportsModelVisitor visitor = new MapperFactoryImplImportsModelVisitor();
    sourceClass.accept(visitor);
    return visitor.getImports(sourceClass.getPackageName().getName());
  }

  private boolean shouldGenerateMapperFactoryInterface(DASourceClass sourceClass) {
    return MAPPER_FACTORY_INTERFACE_INTANTIATIONTYPES.contains(sourceClass.getInstantiationType());
  }

  private boolean shouldGenerateMapperImpl(DASourceClass sourceClass) {
    return !shouldGenerateMapperFactoryInterface(sourceClass);
  }

  private boolean shouldGenerateMapperFactoryImpl(DASourceClass sourceClass) {
    return shouldGenerateMapperFactoryInterface(sourceClass);
  }
}
