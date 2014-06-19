package fr.javatronic.damapping.intellij.plugin.integration.index;

import fr.javatronic.damapping.intellij.plugin.integration.provider.Common;

import java.util.HashMap;
import java.util.Map;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ScalarIndexExtension;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * MapperNameIndex -
 *
 * @author Sébastien Lesaint
 */
public abstract class MapperNameIndex extends ScalarIndexExtension<String> {

  private static final FileBasedIndex.InputFilter JAVA_SOURCE_FILE_INPUT_FILTER = new FileBasedIndex.InputFilter() {
    @Override
    public boolean acceptInput(VirtualFile file) {
      return file.getFileType() instanceof JavaFileType;
    }
  };

  @NotNull
  @Override
  public DataIndexer<String, Void, FileContent> getIndexer() {
    return new DataIndexer<String, Void, FileContent>() {
      @NotNull
      @Override
      public Map<String, Void> map(FileContent inputData) {
        PsiJavaFile psiJavaFile = (PsiJavaFile) inputData.getPsiFile();
        Map<String, Void> res = new HashMap<String, Void>(psiJavaFile.getClasses().length);
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
          if (Common.hasMapperAnnotation(psiClass)) {
            res.put(getKey(psiClass), null);
          }
        }
        return res;
      }
    };
  }

  protected abstract String getKey(PsiClass psiClass);

  @Override
  public KeyDescriptor<String> getKeyDescriptor() {
    return new EnumeratorStringDescriptor();
  }

  @Override
  public FileBasedIndex.InputFilter getInputFilter() {
    return JAVA_SOURCE_FILE_INPUT_FILTER;
  }

  @Override
  public boolean dependsOnFileContent() {
    return true;
  }
}
