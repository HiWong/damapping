package fr.javatronic.damapping.intellij.plugin.integration.index;

import fr.javatronic.damapping.intellij.plugin.integration.component.project.ParseAndGenerateManager;
import fr.javatronic.damapping.intellij.plugin.integration.provider.Common;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

import static fr.javatronic.damapping.processor.util.FluentIterableProxy.toSet;

/**
 * GeneratedClassSimpleNameIndex - Index VirtualFile of classes annoted with @Mapper by the simple name of the
 * generated classes/interfaces. This index is a cheap and efficient way of implementing
 * {@link fr.javatronic.damapping.intellij.plugin.integration.cache.DAMappingPsiShortNamesCache}.
 *
 * @author Sébastien Lesaint
 */
public class GeneratedClassSimpleNameIndex extends AbstractPsiClassIndex {
  public static final ID<String,Void> NAME = ID.create("GeneratedClassSimpleNameIndex");

  @NotNull
  @Override
  public ID<String, Void> getName() {
    return NAME;
  }

  @Override
  public int getVersion() {
    return 0;
  }

  @Override
  protected boolean filter(PsiClass psiClass) {
    return Common.hasMapperAnnotation(psiClass);
  }

  @Override
  protected Set<String> getKeys(PsiClass psiClass) {
    ParseAndGenerateManager parseAndGenerateManager = ParseAndGenerateManager
        .getInstance(psiClass.getProject());

    List<PsiClass> generatedPsiClasses = parseAndGenerateManager
        .getGeneratedPsiClasses(psiClass, GlobalSearchScope.allScope(psiClass.getProject()));

    return toSet(FluentIterable.from(generatedPsiClasses).transform(PsiClassToName.INSTANCE));
  }

  private static enum PsiClassToName implements Function<PsiClass, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(@Nullable PsiClass psiClass) {
      return psiClass.getName();
    }
  }
}
