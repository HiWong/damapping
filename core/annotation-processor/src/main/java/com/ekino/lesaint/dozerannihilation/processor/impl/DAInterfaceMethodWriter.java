package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * DAInterfaceMethodWriter - Writer pour les méthodes d'une interface
 *
 * @author Sébastien Lesaint
 */
public class DAInterfaceMethodWriter<T extends DAWriter> extends AbstractDAWriter<T> {
    private final String name;
    private final DAType returnType;
    private List<DAType> annotations = Collections.emptyList();
    private List<DAParameter> params = Collections.<DAParameter>emptyList();

    public DAInterfaceMethodWriter(String name, DAType returnType, BufferedWriter bw, int indent, T parent) {
        super(bw, parent, indent);
        this.name = name;
        this.returnType = returnType;
    }

    public DAInterfaceMethodWriter<T> withAnnotations(List<DAType> annotations) {
        this.annotations = annotations == null ? Collections.<DAType>emptyList() : ImmutableList.copyOf(annotations);
        return this;
    }

    public DAInterfaceMethodWriter<T> withParams(List<DAParameter> params) {
        this.params = params == null ? Collections.<DAParameter>emptyList() : ImmutableList.copyOf(params);
        return this;
    }

    public T write() throws IOException {
        appendAnnotations(annotations);
        appendIndent();
        appendReturnType();
        bw.append(name);
        appendParams(bw, params);
        bw.append(";");
        bw.newLine();
        bw.newLine();
        return parent;
    }

    private void appendReturnType() throws IOException {
        appendType(bw, returnType);
        bw.append(" ");
    }

    /**
     * Ajoute les parenthèses et les paramètres d'une méthode, les paramètres étant représentés, dans l'ordre
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

}
