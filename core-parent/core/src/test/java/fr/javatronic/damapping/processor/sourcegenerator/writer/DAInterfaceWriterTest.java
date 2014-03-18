/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAModifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.testng.annotations.Test;

import static fr.javatronic.damapping.processor.sourcegenerator.writer.CommonMethodsImpl.INDENT;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAInterfaceWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAInterfaceWriterTest {
  @Test
  public void empty_interface() throws Exception {
    TestWriters testWriters = new TestWriters();
    daInterfaceWriter(testWriters, "name").start().end();

    assertThat(testWriters.getRes()).isEqualTo(
        INDENT + "interface name {" + LINE_SEPARATOR
            + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
    );
  }

  @Test
  public void public_empty_interface() throws Exception {
    TestWriters testWriters = new TestWriters();
    daInterfaceWriter(testWriters, "name")
        .withModifiers(ImmutableSet.of(DAModifier.PUBLIC))
        .start().end();

    assertThat(testWriters.getRes()).isEqualTo(
        INDENT + "public interface name {" + LINE_SEPARATOR
            + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
    );
  }

  @Test
  public void annoted_empty_interface() throws Exception {
    TestWriters testWriters = new TestWriters();
    daInterfaceWriter(testWriters, "name")
        .withAnnotations(ImmutableList.of(NULLABLE_ANNOTATION))
        .start().end();

    assertThat(testWriters.getRes()).isEqualTo(INDENT + "@Nullable" + LINE_SEPARATOR
        + INDENT + "interface name {" + LINE_SEPARATOR
        + LINE_SEPARATOR
        + INDENT + "}" + LINE_SEPARATOR
    );
  }

  @Test
  public void empty_interface_extends_once() throws Exception {
    TestWriters testWriters = new TestWriters();
    daInterfaceWriter(testWriters, "name")
        .withExtended(ImmutableList.of(FUNCTION_INTEGER_TO_STRING_INTERFACE))
        .start().end();

    assertThat(testWriters.getRes()).isEqualTo(
        INDENT + "interface name extends Function<Integer, String> {" + LINE_SEPARATOR +
            LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
    );
  }

  @Test
  public void empty_interface_extends_twice() throws Exception {
    TestWriters testWriters = new TestWriters();
    daInterfaceWriter(testWriters, "name")
        .withExtended(ImmutableList.of(SERIALIZABLE_INTERFACE,
            FUNCTION_INTEGER_TO_STRING_INTERFACE
        )
        )
        .start().end();

    assertThat(testWriters.getRes()).isEqualTo(
        INDENT + "interface name extends Serializable, Function<Integer, String> {" + LINE_SEPARATOR
            + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
    );
  }

  @Test
  public void one_method_interface() throws Exception {
    TestWriters testWriters = new TestWriters();
    daInterfaceWriter(testWriters, "name")
        .start()
        .newMethod("methodName", FUNCTION_INTEGER_TO_STRING_INTERFACE)
        .withAnnotations(ImmutableList.of(OVERRIDE_ANNOTATION))
        .write()
        .end();

    assertThat(testWriters.getRes()).isEqualTo(INDENT + "interface name {" + LINE_SEPARATOR
        + LINE_SEPARATOR
        + INDENT + INDENT + "@Override" + LINE_SEPARATOR
        + INDENT + INDENT + "Function<Integer, String> methodName();" + LINE_SEPARATOR
        + LINE_SEPARATOR
        + INDENT + "}" + LINE_SEPARATOR
    );
  }

  private DAInterfaceWriter<DAWriter> daInterfaceWriter(TestWriters testWriters, String interfaceName) {
    DAWriter parent = new DAWriter() {

    };
    return new DAInterfaceWriter<DAWriter>(interfaceName, testWriters.bw, parent, 1);
  }
}