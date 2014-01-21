/*
 * Copyright 2013 Sébastien Lesaint
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.phan.damapping.processor.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import fr.phan.damapping.processor.model.visitor.DAModelVisitable;
import fr.phan.damapping.processor.model.visitor.DAModelVisitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
* DAMethod -
*
* @author Sébastien Lesaint
*/
@Immutable
public class DAMethod implements DAModelVisitable {
    /**
     * Le ElementKind de la méthode : soit {@link ElementKind.CONSTRUCTOR}, soit {@ŀink ElementKind.METHOD}
     */
    @Nonnull
    private final ElementKind kind;
    /**
     * nom de la méthode
     */
    @Nonnull
    private final DAName name;
    /**
     * modifiers de la méthode (private, final, static, abstract, ...)
     */
    @Nonnull
    private final Set<Modifier> modifiers;
    /**
     * le type de retour de la méthode. Null si la méthode est un constructeur
     */
    @Nullable
    private final DAType returnType; // TOIMPROVE : attention au cas des primitifs si on ajoute @MapperMethod !
    /**
     * Paramètres de la méthode
     */
    @Nonnull
    private final List<DAParameter> parameters;
    /**
     * non utilisé tant que pas de @MapperMethod, pour l'instant on utilise
     * {@link fr.phan.damapping.processor.model.predicate.DAMethodPredicates#isGuavaFunction()}
     */
    private final boolean mapperMethod;
    /**
     * Indique si cette méthode était annotée avec @MapperFactoryMethod
     */
    private final boolean mapperFactoryMethod;

    private DAMethod(Builder builder) {
        this.kind = builder.kind;
        this.name = builder.name;
        this.modifiers = builder.modifiers == null ? Collections.<Modifier>emptySet() : ImmutableSet.copyOf(builder.modifiers);
        this.returnType = builder.returnType;
        this.parameters = builder.parameters == null ? Collections.<DAParameter>emptyList() : ImmutableList.copyOf(builder.parameters);
        this.mapperMethod = builder.mapperMethod;
        this.mapperFactoryMethod = builder.mapperFactoryMethod;
    }

    public static Builder builder(@Nonnull ElementKind kind) {
        return new Builder(kind);
    }

    @Nonnull
    public ElementKind getKind() {
        return kind;
    }

    @Nonnull
    public DAName getName() {
        return name;
    }

    @Nonnull
    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    @Nullable
    public DAType getReturnType() {
        return returnType;
    }

    @Nonnull
    public List<DAParameter> getParameters() {
        return parameters;
    }

    public boolean isMapperMethod() {
        return mapperMethod;
    }

    public boolean isMapperFactoryMethod() {
        return mapperFactoryMethod;
    }

    @Override
    public void accept(DAModelVisitor visitor) {
        visitor.visit(this);
    }

    public static class Builder {
        @Nonnull
        private final ElementKind kind;
        @Nonnull
        private DAName name;
        @Nullable
        private Set<Modifier> modifiers;
        @Nullable
        private DAType returnType;
        @Nullable
        private List<DAParameter> parameters;
        private boolean mapperMethod;
        private boolean mapperFactoryMethod;

        public Builder(@Nonnull ElementKind kind) {
            checkArgument(kind == ElementKind.CONSTRUCTOR || kind == ElementKind.METHOD,
                    "ElementKind of a DAMethod instance can only be either CONSTRUCTOR or METHOD"
            );
            this.kind = kind;
        }

        public Builder withName(@Nonnull DAName name) {
            this.name = name;
            return this;
        }

        public Builder withModifiers(@Nullable Set<Modifier> modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public Builder withReturnType(@Nullable DAType returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder withParameters(@Nullable List<DAParameter> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder withMapperMethod(boolean mapperMethod) {
            this.mapperMethod = mapperMethod;
            return this;
        }

        public Builder withMapperFactoryMethod(boolean mapperFactoryMethod) {
            this.mapperFactoryMethod = mapperFactoryMethod;
            return this;
        }

        public DAMethod build() {
            return new DAMethod(this);
        }
    }
}