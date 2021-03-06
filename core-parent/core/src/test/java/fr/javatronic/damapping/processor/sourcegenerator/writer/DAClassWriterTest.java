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
package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.processor.model.factory.DATypeFactory;

import java.util.Collections;
import com.google.common.collect.ImmutableList;

import org.testng.annotations.Test;

import static fr.javatronic.damapping.processor.model.constants.JavaIOConstants.SERIALIZABLE_TYPE;
import static fr.javatronic.damapping.processor.model.constants.JavaLangConstants.OVERRIDE_ANNOTATION;
import static fr.javatronic.damapping.processor.model.constants.Jsr305Constants.NULLABLE_ANNOTATION;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.CommonMethodsImpl.INDENT;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.DAWRITER_ABSTACT_CLASS;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.LINE_SEPARATOR;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.NAME_DATYPE;
import static fr.javatronic.damapping.processor.sourcegenerator.writer.DAWriterTestUtil.STRING_TITI_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAClassWriterTest -
 * <p/>
 * TODO finir les test U de DAClassWriter
 * <ul>
 * <li>tester l'objet retourné par chaque méthode (ie. vérifier le codage de la fluent) ?</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAClassWriterTest {

  @Test
  public void empty_empty_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext).start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "class Name {" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_public_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext).withModifiers(DAModifier.PUBLIC).start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "public class Name {" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_public_final_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext).withModifiers(DAModifier.PUBLIC, DAModifier.FINAL).start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "public final class Name {" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_class_annoted_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext).withAnnotations(ImmutableList.of(OVERRIDE_ANNOTATION)).start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "@Override" + LINE_SEPARATOR +
            INDENT + "class Name {" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_class_annoted_twice_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext)
        .withAnnotations(ImmutableList.of(OVERRIDE_ANNOTATION, NULLABLE_ANNOTATION))
        .start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "@Override" + LINE_SEPARATOR +
            INDENT + "@Nullable" + LINE_SEPARATOR +
            INDENT + "class Name {" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_class_implements_once() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext).withImplemented(ImmutableList.of(SERIALIZABLE_TYPE)).start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "class Name implements Serializable {" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_class_implements_twice_one_with_types() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext).withImplemented(
        ImmutableList.of(SERIALIZABLE_TYPE, FUNCTION_INTEGER_TO_STRING_INTERFACE)
    ).start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(
            INDENT + "class Name implements Serializable, Function<Integer, String> {" + LINE_SEPARATOR
                + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_class_extends() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext).withExtended(DAWRITER_ABSTACT_CLASS).start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "class Name extends DAWriter {" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_class_extends_one_with_types() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext).withExtended(BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS).start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "class Name extends Bidon<Integer, String> {" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void empty_public_final_class_annoted_with_extends_and_implements() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext)
        .withModifiers(DAModifier.PUBLIC, DAModifier.FINAL)
        .withAnnotations(ImmutableList.of(OVERRIDE_ANNOTATION))
        .withImplemented(ImmutableList.of(FUNCTION_INTEGER_TO_STRING_INTERFACE))
        .withExtended(BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS)
        .start().end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "@Override" + LINE_SEPARATOR
            + INDENT + "public final class Name extends Bidon<Integer, String> implements Function<Integer, " +
            "String> {" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void end_returns_parent_writer() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    DAWriter parent = new DAWriter() {

    };
    DAClassWriter<DAWriter> classWriter = new DAClassWriter<DAWriter>(NAME_DATYPE, fileContext,
        parent, 1
    );

    assertThat(classWriter.end()).isSameAs(parent);
  }

  @Test
  public void one_method_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext)
        .start()
        .newMethod("methodName", DATypeFactory.from(String.class)).start().end()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "class Name {" + LINE_SEPARATOR
            + LINE_SEPARATOR
            + INDENT + INDENT + "String methodName() {" + LINE_SEPARATOR
            + INDENT + INDENT + "}" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void explicite_default_constructor_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext)
        .start()
        .newConstructor().start().end()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "class Name {" + LINE_SEPARATOR
            + LINE_SEPARATOR
            + INDENT + INDENT + "Name() {" + LINE_SEPARATOR
            + INDENT + INDENT + "}" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void constructor_with_params_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext)
        .start()
        .newConstructor()
        .withParams(ImmutableList.of(STRING_TITI_PARAMETER))
        .start()
        .newStatement()
        .start()
        .append("this.toto = toto")
        .end()
        .end()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "class Name {" + LINE_SEPARATOR
            + LINE_SEPARATOR
            + INDENT + INDENT + "Name(String titi) {" + LINE_SEPARATOR
            + INDENT + INDENT + INDENT + "this.toto = toto;" + LINE_SEPARATOR
            + INDENT + INDENT + "}" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void one_property_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext)
        .start()
        .newProperty("variableName", DATypeFactory.from(String.class))
        .withAnnotations(ImmutableList.of(NULLABLE_ANNOTATION))
        .write()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "class Name {" + LINE_SEPARATOR
            + INDENT + INDENT + "@Nullable" + LINE_SEPARATOR
            + INDENT + INDENT + "String variableName;" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void one_property_one_method_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext)
        .start()
        .newProperty("variableName", DATypeFactory.from(String.class))
        .write()
        .newMethod("methodName", DATypeFactory.from(String.class))
        .withAnnotations(ImmutableList.of(NULLABLE_ANNOTATION)).start().end()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "class Name {" + LINE_SEPARATOR
            + INDENT + INDENT + "String variableName;" + LINE_SEPARATOR
            + LINE_SEPARATOR
            + INDENT + INDENT + "@Nullable" + LINE_SEPARATOR
            + INDENT + INDENT + "String methodName() {" + LINE_SEPARATOR
            + INDENT + INDENT + "}" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void one_initialized_property_class() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();
    daClassWriter(fileContext)
        .start()
        .newInitializedProperty("variableName", NAME_DATYPE)
        .initialize()
        .append("new ").appendType(NAME_DATYPE)
        .appendParamValues(Collections.<DAParameter>emptyList())
        .end()
        .end()
        .end();

    assertThat(fileContext.getRes())
        .isEqualTo(INDENT + "class Name {" + LINE_SEPARATOR
            + INDENT + INDENT + "Name variableName = new Name();" + LINE_SEPARATOR
            + INDENT + "}" + LINE_SEPARATOR
        );
  }

  @Test
  public void newClass_create_new_DAClassWriter() throws Exception {
    FileContextTestImpl fileContext = new FileContextTestImpl();

    DAClassWriter<DAWriter> classWriter = daClassWriter(fileContext);
    DAType classType = DATypeFactory.from(String.class);

    DAClassWriter<DAClassWriter<DAWriter>> newClassWriter = classWriter.newClass(classType);

    assertThat(newClassWriter.classType).isSameAs(classType);
    assertThat(newClassWriter.parent).isSameAs(classWriter);
    assertThat(newClassWriter.commons.getIndentOffset()).isEqualTo(2);
    assertThat(newClassWriter.commons.getFileContext()).isSameAs(fileContext);
  }

  private DAClassWriter<DAWriter> daClassWriter(FileContextTestImpl fileContext) {
    DAWriter parent = new DAWriter() {

    };
    return new DAClassWriter<DAWriter>(NAME_DATYPE, fileContext, parent, 1);
  }

}
