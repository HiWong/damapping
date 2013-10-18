package com.ekino.lesaint.dozerannihilation.processor.impl;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.lang.model.type.TypeKind;
import java.util.Collections;
import java.util.List;

/**
 * DATypeFactory -
 *
 * @author Sébastien Lesaint
 */
public final class DATypeFactory {
    private DATypeFactory() {
        // prevents instantiation
    }

    /**
     * Créer un objet DAType à partir de la Class d'une enum/classe/annotation.
     * <br/>
     * La liste <code>typeArgs</code> est vide.
     *
     * @param clazz une {@link Class}
     * @return un objet {@link DAType}
     */
    static DAType from(@Nonnull Class<?> clazz) {
        return instance(clazz.getSimpleName(), clazz.getCanonicalName(), Collections.<DAType>emptyList());
    }

    /**
     * Créer un objet DAType à partir de la Class d'une enum/classe/annotation et la liste typeArgs.
     *
     * @param clazz une {@link Class}
     * @return un objet {@link DAType}
     */
    static DAType from(@Nonnull Class<?> clazz, @Nonnull List<DAType> typeArgs) {
        return instance(clazz.getSimpleName(), clazz.getCanonicalName(), typeArgs);
    }

    /**
     * Créer un objet DAType de type <code>TypeKind.DECLARED</code> à partir du nom qualifié d'une enum/classe/interface.
     * <br/>
     * Le simpleName de l'objet retourné est inféré à partir du qualifiedName spécifié comme la sous string commençant
     * au caractère après le dernier point.
     * <br/>
     * La liste <code>typeArgs</code> est vide.
     *
     * @param qualifiedName un {@link String}
     * @return un objet {@link DAType}
     */
    static DAType declared(@Nonnull String qualifiedName) {
        return declared(qualifiedName, Collections.<DAType>emptyList());
    }

    /**
     * Créer un objet DAType de type <code>TypeKind.DECLARED</code> à partir du nom qualifié d'une enum/classe/interface
     * et la liste typeArgs.
     * <br/>
     * Le simpleName de l'objet retourné est inféré à partir du qualifiedName spécifié comme la sous string commençant
     * au caractère après le dernier point
     *
     * @param qualifiedName un {@link String}
     * @param typeArgs      une {@link List} de {@link DAType}
     *
     * @return un objet {@link DAType}
     */
    static DAType declared(@Nonnull String qualifiedName, @Nonnull List<DAType> typeArgs) {
        String simpleName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
        return instance(simpleName, qualifiedName, typeArgs);
    }

    private static DAType instance(String simpleName, String qualifiedName, List<DAType> typeArgs) {
        DAType annotationDAType = new DAType();
        annotationDAType.kind = TypeKind.DECLARED;
        annotationDAType.simpleName = DANameFactory.from(simpleName);
        annotationDAType.qualifiedName = DANameFactory.from(qualifiedName);
        annotationDAType.typeArgs = Preconditions.checkNotNull(typeArgs);
        return annotationDAType;
    }
}