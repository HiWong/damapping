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
package fr.javatronic.damapping.test.guava;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.test.mappermethod.Dummy_1Mapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Function;

/**
 * WildcardGenerics - Demonstrates support for a class annotated with {@code @Mapper} which declares no default
 * constructor but a constructor with a single parameter which is itself a generate Mapper interface from another
 * package.
 * <p>
 * This coding structure (ie. a dedicated class using a generated Mapper interface) is the basic pattern for mapping
 * trees of beans.
 * </p>
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class ConstructorWithMapperFromOtherPackageAsParameter implements Function<Integer, String> {
  private final Dummy_1Mapper dummy_1;

  public ConstructorWithMapperFromOtherPackageAsParameter(@Nonnull Dummy_1Mapper dummy_1) {
    this.dummy_1 = dummy_1;
  }

  @Nullable
  @Override
  public String apply(@Nullable Integer input) {
    return "resultDoesNotMatter";
  }
}
