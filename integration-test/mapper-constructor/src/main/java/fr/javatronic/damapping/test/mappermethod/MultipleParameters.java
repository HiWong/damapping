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
package fr.javatronic.damapping.test.mappermethod;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.test.guava.subpackage.OutOfPackage;

import java.math.BigDecimal;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Optional;

/**
 * MultipleParameters - This mapper is designed to test support for multiple parameters in implicit mapper method, it
 * covers parameters annotations, type from current package and subpackage, generics and arrays.
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class MultipleParameters {

  public List<BigDecimal> map(@Nonnull OutOfPackage paramA, @Nullable InPackage paramB, Optional<Object[]> objs) {
    return null; // implementation does not matter
  }
}
