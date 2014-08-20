package fr.javatronic.damapping.processor.sourcegenerator.writer;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAType;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * CommonMethods -
 *
 * @author Sébastien Lesaint
 */
interface CommonMethods extends Appendable, Closeable, Flushable {
  BufferedWriter getBufferedWriter();

  /**
   * The indent offset.
   * Starts with 0.
   *
   * @return a int
   */
  int getIndentOffset();

  void appendIndent() throws IOException;

  void appendModifiers(Set<DAModifier> modifiers) throws IOException;

  void appendAnnotations(Collection<DAAnnotation> annotations) throws IOException;

  void appendType(DAType type) throws IOException;

  void appendTypeArgs(List<DAType> typeArgs) throws IOException;

  void newLine() throws IOException;
}
