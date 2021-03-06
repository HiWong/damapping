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

import fr.javatronic.damapping.processor.model.DAAnnotation;
import fr.javatronic.damapping.processor.model.DAModifier;
import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.processor.model.DAType;
import fr.javatronic.damapping.util.Lists;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * DAConstructorWriter - Writer pour les constructeurs d'une classe
 * <p>
 * TODO améliorations de DAConstructorWriter
 * </p>
 * <ul>
 * <li>contrôle sur les modifiers : seulement public, private ou protected</li>
 * <li>ajouter convenience method pour les invocations de super</li>
 * <li>ajout de vérification d'état, pas possible d'appeller super si un statement a été créé</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAConstructorWriter<T extends DAWriter> extends AbstractDAWriter<T> {
  private final String name;
  @Nullable
  private DAModifier[] modifiers;
  @Nonnull
  private List<DAAnnotation> annotations = Collections.emptyList();
  private List<DAParameter> params = Collections.<DAParameter>emptyList();

  public DAConstructorWriter(DAType constructedType, FileContext fileContext, T parent, int indentOffset) {
    super(fileContext, parent, indentOffset);
    this.name = constructedType.getSimpleName().getName();
  }

  public DAConstructorWriter<T> withAnnotations(@Nullable List<DAAnnotation> annotations) {
    this.annotations = annotations == null ? Collections.<DAAnnotation>emptyList() : Lists.copyOf(annotations);
    return this;
  }

  public DAConstructorWriter<T> withModifiers(@Nullable DAModifier... modifiers) {
    this.modifiers = modifiers;
    return this;
  }

  public DAConstructorWriter<T> withParams(List<DAParameter> params) {
    this.params = params == null ? Collections.<DAParameter>emptyList() : Lists.copyOf(params);
    return this;
  }

  public DAConstructorWriter<T> start() throws IOException {
    commons.appendAnnotations(annotations);
    commons.appendIndent();
    commons.appendModifiers(modifiers);
    commons.append(name);
    appendParams(params);
    commons.append(" {");
    commons.newLine();
    return this;
  }

  /**
   * Ajoute les parenthèses et les paramètres du constructeur, les paramètres étant représentés, dans l'ordre
   * par la liste de DAType en argument.
   */
  private void appendParams(List<DAParameter> params) throws IOException {
    if (params.isEmpty()) {
      commons.append("()");
      return;
    }

    commons.append("(");
    Iterator<DAParameter> it = params.iterator();
    while (it.hasNext()) {
      DAParameter parameter = it.next();
      commons.appendInlineAnnotations(parameter.getAnnotations());
      commons.appendType(parameter.getType());
      commons.append(" ").append(parameter.getName());
      if (it.hasNext()) {
        commons.append(", ");
      }
    }
    commons.append(")");
  }

  public DAStatementWriter<DAConstructorWriter<T>> newStatement() {
    return new DAStatementWriter<DAConstructorWriter<T>>(commons.getFileContext(), this,
        commons.getIndentOffset() + 1
    );
  }

  public T end() throws IOException {
    commons.appendIndent();
    commons.append("}");
    commons.newLine();
    return parent;
  }
}
