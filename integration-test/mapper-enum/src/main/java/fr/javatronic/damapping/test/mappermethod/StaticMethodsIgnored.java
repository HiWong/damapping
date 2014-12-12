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

/**
 * This mapper is designed to test that the static method is not identified as a  mapper method.
 */
@Mapper
public enum StaticMethodsIgnored {
  INSTANCE;

  // this method is not a mapper method
  public static String apply(Integer i) {
    return null;
  }

  public String map(Integer input) {
    return null;
  }
}
