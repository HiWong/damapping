package fr.phan.damapping.processor.impl;

import fr.phan.damapping.processor.sourcegenerator.FileGeneratorContext;
import fr.phan.damapping.processor.sourcegenerator.SourceGenerator;
import fr.phan.damapping.processor.sourcegenerator.SourceWriterDelegate;

import java.io.BufferedWriter;
import java.io.IOException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * JavaxSourceWriterDelegate -
 *
 * @author: Sébastien Lesaint
 */
public class JavaxSourceWriterDelegate implements SourceWriterDelegate {
  private final ProcessingEnvironment processingEnv;
  private final TypeElement contextElement;

  public JavaxSourceWriterDelegate(ProcessingEnvironment processingEnv, TypeElement contextElement) {
    this.processingEnv = processingEnv;
    this.contextElement = contextElement;
  }

  @Override
  public void generateFile(SourceGenerator sourceGenerator, FileGeneratorContext context) throws IOException {
    JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
        sourceGenerator.fileName(context),
        contextElement
    );
    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "generating " + jfo.toUri());

    sourceGenerator.writeFile(new BufferedWriter(jfo.openWriter()), context);
  }
}
