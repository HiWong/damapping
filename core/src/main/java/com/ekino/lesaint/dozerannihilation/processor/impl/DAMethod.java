package com.ekino.lesaint.dozerannihilation.processor.impl;

import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import java.util.List;

/**
* DAMethod -
*
* @author Sébastien Lesaint
*/
class DAMethod extends AbstractImportVisitable {
    ElementKind kind;
    /*nom de la méthode/function*/
    DAName name;
    /*le type de retour de la méthode. Null si la méthode est un constructeur*/
    @Nullable
    DAType returnType; // attention au cas des primitifs si on ajoute @MapperMethod !
    List<DAParameter> parameters;
    /*non utilisé tant que pas de @MapperMethod*/
    boolean mapperMethod;

    public boolean isDefaultConstructor() {
        return kind == ElementKind.CONSTRUCTOR;
    }

    public boolean isGuavaFunction() {
        // TOIMPROVE, check more specific info in the model, can we know if method override from an interface ? we should check the parameter type and the return type
        return kind == ElementKind.METHOD && "apply".equals(name.getName());
    }

    @Override
    protected void visiteForMapper(ImportVisitor visitor) {
        if (isGuavaFunction()) {
            // guava function is not generated in Mapper interface because it is declared by implemented Function interface
            return;
        }
        if (isDefaultConstructor()) {
            // constructor is not generated in Mapper interface
            return;
        }
        for (DAParameter parameter : parameters) {
            visitor.addMapperImport(parameter.type.qualifiedName);
        }
        if (returnType != null) {
            visitor.addMapperImport(returnType.qualifiedName);
        }
    }

    @Override
    protected void visiteForMapperImpl(ImportVisitor visitor) {
        if (isDefaultConstructor()) {
            // constructor is not generated in MapperImpl class
            return;
        }
        for (DAParameter parameter : parameters) {
            visitor.addMapperImport(parameter.type.qualifiedName);
        }
        if (returnType != null) {
            visitor.addMapperImport(returnType.qualifiedName);
        }
    }

    @Override
    protected void visiteForMapperFactory(ImportVisitor visitor) {
        // none
    }
}