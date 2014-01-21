package fr.phan.damapping.processor.impl.javaxparsing;

import com.google.common.base.Optional;
import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DAParameter;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.factory.DANameFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * JavaxExtractor - Extracts objects of the DAMapping model from the objects of the Javax model.
 *
 * @author: Sébastien Lesaint
 */
public interface JavaxExtractor {
    @Nonnull
    DAType extractType(TypeMirror type);

    @Nonnull
    DAType extractType(TypeMirror type, Element element);

    @Nonnull
    DAType extractWildcardType(WildcardType wildcardType);

    @Nonnull
    DAType extractReturnType(ExecutableElement methodElement);

    @Nonnull
    List<DAType> extractTypeArgs(TypeMirror typeMirror);

    @Nonnull
    Set<Modifier> extractModifiers(ExecutableElement methodElement);

    @Nullable
    List<DAParameter> extractParameters(ExecutableElement methodElement);

    @Nullable
    DAName extractSimpleName(TypeMirror type, Element element);

    @Nullable
    DAName extractQualifiedName(TypeMirror type, Element element);

    @Nullable
    DAName extractQualifiedName(DeclaredType o);
}
