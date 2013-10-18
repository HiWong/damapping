package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import javax.lang.model.element.Modifier;

import static com.ekino.lesaint.dozerannihilation.processor.impl.DAWriterTestUtil.LINE_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAFileWriterTest -
 *
 * @author Sébastien Lesaint
 */
public class DAFileWriterTest {

    public static final DAName PACKAGE_NAME = DANameFactory.from("com.acme.toto");

    @Test
    public void empty_file() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw);

        assertThat(testWriters.getRes()).isEqualTo("");
    }

    @Test
    public void package_only() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw).appendPackage(PACKAGE_NAME);

        assertThat(testWriters.getRes()).isEqualTo("package com.acme.toto;" + LINE_SEPARATOR + LINE_SEPARATOR);
    }

    @Test
    public void package_imports_filtered_file() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw)
                .appendPackage(PACKAGE_NAME)
                .appendImports(ImmutableSet.<DAName>of(
                        DAWriterTestUtil.FUNCTION_INTEGER_TO_STRING_INTERFACE.qualifiedName,
                        DAWriterTestUtil.BIDON_INTEGER_TO_STRING_ABSTRACT_CLASS.qualifiedName,
                        DAWriterTestUtil.OVERRIDE_ANNOTATION.qualifiedName
                ));

        assertThat(testWriters.getRes()).isEqualTo("package com.acme.toto;" + LINE_SEPARATOR
                + LINE_SEPARATOR
                + "import com.acme.Bidon;" + LINE_SEPARATOR
                + "import com.google.common.base.Function;" + LINE_SEPARATOR
                + LINE_SEPARATOR);
    }

    @Test
    public void empty_class_file() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw)
                .appendPackage(PACKAGE_NAME)
                .newClass("name")
                .withModifiers(ImmutableSet.of(Modifier.PUBLIC))
                .start()
                .end();

        assertThat(testWriters.getRes()).isEqualTo("package com.acme.toto;" + LINE_SEPARATOR
                + LINE_SEPARATOR
                + "public class name {" + LINE_SEPARATOR
                + LINE_SEPARATOR
                + "}" + LINE_SEPARATOR
        );
    }

    @Test
    public void empty_interface_file() throws Exception {
        TestWriters testWriters = new TestWriters();
        new DAFileWriter(testWriters.bw)
                .appendPackage(PACKAGE_NAME)
                .newInterface("name")
                .start()
                .end();

        assertThat(testWriters.getRes()).isEqualTo("package com.acme.toto;" + LINE_SEPARATOR
                + LINE_SEPARATOR
                + "interface name {" + LINE_SEPARATOR
                + LINE_SEPARATOR
                + "}" + LINE_SEPARATOR
        );
    }
}