package fr.phan.damapping.processor.impl.writer;

import fr.phan.damapping.processor.model.DAType;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;

/**
 * CommonMethods -
 *
 * @author Sébastien Lesaint
 */
interface CommonMethods extends Appendable, Closeable, Flushable {
    BufferedWriter getBufferedWriter();

    int getIndent();

    void appendIndent() throws IOException;

    void appendModifiers(Set<Modifier> modifiers) throws IOException;

    void appendAnnotations(Collection<DAType> annotations) throws IOException;

    void appendType(DAType type) throws IOException;

    void appendTypeArgs(List<DAType> typeArgs) throws IOException;

    void newLine() throws IOException;
}
