package fr.javatronic.damapping.processor.sourcegenerator;

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAMethod;
import fr.javatronic.damapping.util.Lists;
import fr.javatronic.damapping.util.Predicate;
import fr.javatronic.damapping.util.Predicates;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import static fr.javatronic.damapping.processor.model.constants.JavaLangConstants.OVERRIDE_ANNOTATION;
import static fr.javatronic.damapping.util.FluentIterable.from;

/**
 * SourceGeneratorSupport -
 *
 * @author Sébastien Lesaint
 */
public class SourceGeneratorSupport {
  private static final List<DAAnnotation> OVERRIDE_ANNOTATION_AS_LIST = Lists.of(OVERRIDE_ANNOTATION);
  private static final Predicate<DAAnnotation> NOT_OVERRIDE_ANNOTATION = Predicates.not(
      Predicates.equalTo(OVERRIDE_ANNOTATION)
  );

  /**
   * Compute the annotations of the a method overriding another one.
   * <p>
   * The returned list of composed of the {@code @Override} annotation followed by the annotations of the specified method.
   * </p>
   * <p>
   * The {@code @Override} annotation is always the first annnotation and is removed from annotations on the mapper
   * method on the DASourceClass to avoid duplicates.
   * </p>
   *
   * @param mapperMethod a {@link fr.javatronic.damapping.processor.model.DAMethod}
   *
   * @return a {@link fr.javatronic.damapping.util.Lists} of {@link fr.javatronic.damapping.processor.model.DAAnnotation}
   */
  @Nonnull
  public List<DAAnnotation> computeOverrideMethodAnnotations(@Nonnull DAMethod mapperMethod) {
    List<DAAnnotation> annotations = mapperMethod.getAnnotations();
    if (annotations.isEmpty() || (annotations.size() == 1 && OVERRIDE_ANNOTATION.equals(annotations.get(0)))) {
      return OVERRIDE_ANNOTATION_AS_LIST;
    }

    List<DAAnnotation> res = new ArrayList<DAAnnotation>();
    res.add(OVERRIDE_ANNOTATION);
    res.addAll(from(annotations).filter(NOT_OVERRIDE_ANNOTATION).toList());
    return res;
  }
}