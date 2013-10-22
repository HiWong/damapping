package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * DAConstructorWriter - Writer pour les constructeurs d'une classe
 *
 * TODO améliorations de DAConstructorWriter
 * <ul>
 *     <li>contrôle sur les modifiers : seulement public, private ou protected</li>
 *     <li>ajouter convenience method pour les invocations de super</li>
 *     <li>ajout de vérification d'état, pas possible d'appeller super si un statement a été créé</li>
 * </ul>
 *
 * @author Sébastien Lesaint
 */
public class DAConstructorWriter<T extends DAWriter> extends AbstractDAWriter<T> {
    private final String name;
    private Set<Modifier> modifiers = Collections.<Modifier>emptySet();
    private List<DAParameter> params = Collections.<DAParameter>emptyList();

    public DAConstructorWriter(DAType constructedType, BufferedWriter bw, T parent, int indent) {
        super(bw, parent, indent);
        this.name = constructedType.simpleName.getName();
    }

    public DAConstructorWriter<T> withModifiers(Set<Modifier> modifiers) {
        this.modifiers = modifiers == null ? Collections.<Modifier>emptySet() : ImmutableSet.copyOf(modifiers);
        return this;
    }

    public DAConstructorWriter<T> withParams(List<DAParameter> params) {
        this.params = params == null ? Collections.<DAParameter>emptyList() : ImmutableList.copyOf(params);
        return this;
    }

    public DAConstructorWriter<T> start() throws IOException {
        appendIndent();
        appendModifiers(bw, modifiers);
        bw.append(name);
        appendParams(bw, params);
        bw.append(" {");
        bw.newLine();
        return this;
    }

    /**
     * Ajoute les parenthèses et les paramètres du constructeur, les paramètres étant représentés, dans l'ordre
     * par la liste de DAType en argument.
     */
    private void appendParams(BufferedWriter bw, List<DAParameter> params) throws IOException {
        if (params.isEmpty()) {
            bw.append("()");
            return;
        }

        bw.append("(");
        Iterator<DAParameter> it = params.iterator();
        while (it.hasNext()) {
            DAParameter parameter = it.next();
            appendType(bw, parameter.type);
            bw.append(" ").append(parameter.name);
            if (it.hasNext()) {
                bw.append(", ");
            }
        }
        bw.append(")");
    }

    public DAStatementWriter<DAConstructorWriter<T>> newStatement() {
        return new DAStatementWriter<DAConstructorWriter<T>>(bw, this, indent + 1);
    }

    public T end() throws IOException {
        appendIndent();
        bw.append("}");
        bw.newLine();
        bw.newLine();
        return parent;
    }
}
