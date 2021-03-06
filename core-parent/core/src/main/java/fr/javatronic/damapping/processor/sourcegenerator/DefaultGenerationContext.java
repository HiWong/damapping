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
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * DefaultGenerationContext -
 *
 * @author Sébastien Lesaint
 */
public class DefaultGenerationContext implements GenerationContext {
  @Nonnull
  private final DASourceClass sourceClass;
  @Nonnull
  private final Map<String, GeneratedFileDescriptor> descriptors;

  public static class PartialDescriptor {
    private final String key;
    private final DAType daType;
    private final List<DAImport> imports;
    private final SourceGeneratorFactory sourceGeneratorFactory;

    public PartialDescriptor(String key, DAType daType, List<DAImport> imports, SourceGeneratorFactory sourceGeneratorFactory) {
      this.key = key;
      this.daType = daType;
      this.imports = imports;
      this.sourceGeneratorFactory = sourceGeneratorFactory;
    }
  }

  public DefaultGenerationContext(@Nonnull DASourceClass sourceClass, @Nonnull Collection<PartialDescriptor> partialDescriptors) {
    this.sourceClass = checkNotNull(sourceClass);
    Map<String, GeneratedFileDescriptor> res = Maps.newHashMap();
    for (PartialDescriptor partialDescriptor : partialDescriptors) {
      res.put(
          partialDescriptor.key,
          new GeneratedFileDescriptorImpl(partialDescriptor.key, partialDescriptor.daType, partialDescriptor.imports, partialDescriptor.sourceGeneratorFactory, this)
      );
    }
    this.descriptors = Collections.unmodifiableMap(res);
  }

  @Override
  @Nullable
  public GeneratedFileDescriptor getDescriptor(String key) {
    return this.descriptors.get(key);
  }

  @Nonnull
  @Override
  public Set<String> getDescriptorKeys() {
    return this.descriptors.keySet();
  }

  @Override
  @Nonnull
  public DASourceClass getSourceClass() {
    return sourceClass;
  }

}
