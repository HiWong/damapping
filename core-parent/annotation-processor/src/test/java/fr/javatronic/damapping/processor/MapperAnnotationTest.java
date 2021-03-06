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
package fr.javatronic.damapping.processor;

import javax.tools.JavaFileObject;

import com.google.testing.compile.JavaFileObjects;
import org.testng.annotations.Test;

/**
 * MapperAnnotationTest -
 *
 * @author Sébastien Lesaint
 */
public class MapperAnnotationTest extends AbstractCompilationTest {

  @Test
  public void compiling_empty_annotated_class_implementing_Function_is_successfull() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines(
        "MapperOnInterface",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public interface MapperOnInterface {",
        "  Integer apply(String input);",
        "}"
    );

    assertThat(fileObject)
        .failsToCompile()
        .withErrorContaining("Type MapperOnInterface annoted with @Mapper annotation is not a class nor an enum (kind found = INTERFACE)")
        .in(fileObject)
        .onLine(4);
  }

}
