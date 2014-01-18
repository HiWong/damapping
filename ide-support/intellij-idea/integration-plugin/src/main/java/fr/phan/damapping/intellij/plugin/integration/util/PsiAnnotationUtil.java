package fr.phan.damapping.intellij.plugin.integration.util;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.util.StringBuilderSpinAllocator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * PsiAnnotationUtil - Fork from Lombok Plugin
 *
 * @author peichhorn
 * @author Plushnikov Michail
 */
public class PsiAnnotationUtil {

    @Nullable
    public static PsiAnnotation findAnnotation(@NotNull PsiModifierListOwner psiModifierListOwner, @NotNull String qualifiedName) {
        final PsiModifierList annotationOwner = psiModifierListOwner.getModifierList();
        if (annotationOwner == null) {
            return null;
        }

        final PsiAnnotation[] annotations = annotationOwner.getAnnotations();
        if (annotations.length == 0) {
            return null;
        }

        final String shortName = StringUtil.getShortName(qualifiedName);
        for (PsiAnnotation annotation : annotations) {
            PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
            if (referenceElement != null && shortName.equals(referenceElement.getReferenceName())) {
                if (qualifiedName.equals(annotation.getQualifiedName())) {
                    return annotation;
                }
            }
        }

        return null;
    }

    public static boolean isAnnotatedWith(@NotNull PsiModifierListOwner psiModifierListOwner, @NotNull final Class<? extends Annotation> annotationType) {
        return null != findAnnotation(psiModifierListOwner, annotationType.getName());
    }

    public static boolean isNotAnnotatedWith(@NotNull PsiModifierListOwner psiModifierListOwner, @NotNull final Class<? extends Annotation> annotationType) {
        return !isAnnotatedWith(psiModifierListOwner, annotationType);
    }

    public static boolean isAnnotatedWith(@NotNull PsiModifierListOwner psiModifierListOwner, @NotNull final Class<? extends Annotation>... annotationTypes) {
        final PsiModifierList annotationOwner = psiModifierListOwner.getModifierList();
        if (annotationOwner == null) {
            return false;
        }

        final PsiAnnotation[] annotations = annotationOwner.getAnnotations();
        if (annotations.length == 0) {
            return false;
        }

        for (Class<? extends Annotation> annotationType : annotationTypes) {
            final String shortName = annotationType.getSimpleName();
            for (PsiAnnotation annotation : annotations) {
                PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
                if (referenceElement != null && shortName.equals(referenceElement.getReferenceName())) {
                    String qualifiedName = annotationType.getName();
                    if (qualifiedName.equals(annotation.getQualifiedName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isNotAnnotatedWith(@NotNull PsiModifierListOwner psiModifierListOwner, @NotNull final Class<? extends Annotation>... annotationTypes) {
        return !isAnnotatedWith(psiModifierListOwner, annotationTypes);
    }

    public static boolean isAnnotatedWith(@NotNull PsiModifierListOwner psiModifierListOwner, @NotNull final Pattern annotationPattern) {
        final PsiModifierList psiModifierList = psiModifierListOwner.getModifierList();
        if (psiModifierList != null) {
            for (PsiAnnotation psiAnnotation : psiModifierList.getAnnotations()) {
                final String suspect = getSimpleNameOf(psiAnnotation);
                if (annotationPattern.matcher(suspect).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    public static String getSimpleNameOf(@NotNull PsiAnnotation psiAnnotation) {
        PsiJavaCodeReferenceElement referenceElement = psiAnnotation.getNameReferenceElement();
        return StringUtil.notNullize(null == referenceElement ? null : referenceElement.getReferenceName());
    }


    @Nullable
    public static <T> T getAnnotationValue(@NotNull PsiAnnotation psiAnnotation, Class<T> asClass) {
        return getAnnotationValue(psiAnnotation, PsiAnnotation.DEFAULT_REFERENCED_METHOD_NAME, asClass);
    }

    @NotNull
    public static <T> Collection<T> getAnnotationValues(@NotNull PsiAnnotation psiAnnotation, @NotNull String parameter, @NotNull Class<T> asClass) {
        Collection<T> result = Collections.emptyList();
        PsiAnnotationMemberValue attributeValue = psiAnnotation.findAttributeValue(parameter);
        if (attributeValue instanceof PsiArrayInitializerMemberValue) {
            final PsiAnnotationMemberValue[] memberValues = ((PsiArrayInitializerMemberValue) attributeValue).getInitializers();
            result = new ArrayList<T>(memberValues.length);

            for (PsiAnnotationMemberValue memberValue : memberValues) {
                T value = resolveElementValue(memberValue, asClass);
                if (null != value) {
                    result.add(value);
                }
            }
        } else if (null != attributeValue) {
            T value = resolveElementValue(attributeValue, asClass);
            if (null != value) {
                result = Collections.singletonList(value);
            }
        }
        return result;
    }

    @Nullable
    public static <T> T getAnnotationValue(@NotNull PsiAnnotation psiAnnotation, @NotNull String parameter, @NotNull Class<T> asClass) {
        T result = null;
        PsiAnnotationMemberValue attributeValue = psiAnnotation.findAttributeValue(parameter);
        if (null != attributeValue) {
            result = resolveElementValue(attributeValue, asClass);
        }
        return result;
    }

    @Nullable
    private static <T> T resolveElementValue(@NotNull PsiElement psiElement, @NotNull Class<T> asClass) {
        T value = null;
        if (psiElement instanceof PsiReferenceExpression) {
            final PsiElement resolved = ((PsiReferenceExpression) psiElement).resolve();

            if (resolved instanceof PsiEnumConstant) {
                final PsiEnumConstant psiEnumConstant = (PsiEnumConstant) resolved;
                //Enums are supported as VALUE-Strings only
                if (asClass.isAssignableFrom(String.class)) {
                    value = (T) psiEnumConstant.getName();
                }
            } else if (resolved instanceof PsiVariable) {
                final PsiVariable psiVariable = (PsiVariable) resolved;
                Object elementValue = psiVariable.computeConstantValue();
                if (null != elementValue && asClass.isAssignableFrom(elementValue.getClass())) {
                    value = (T) elementValue;
                }
            }
        } else if (psiElement instanceof PsiLiteralExpression) {
            Object elementValue = ((PsiLiteralExpression) psiElement).getValue();
            if (null != elementValue && asClass.isAssignableFrom(elementValue.getClass())) {
                value = (T) elementValue;
            }
        } else if (psiElement instanceof PsiClassObjectAccessExpression) {
            PsiTypeElement elementValue = ((PsiClassObjectAccessExpression) psiElement).getOperand();
            //Enums are supported as VALUE-Strings only
            if (asClass.isAssignableFrom(PsiType.class)) {
                value = (T) elementValue.getType();
            }
        }
        return value;
    }

    @Nullable
    public static <T> T getDeclaredAnnotationValue(@NotNull PsiAnnotation psiAnnotation, @NotNull String parameter, @NotNull Class<T> asClass) {
        T value = null;
        PsiAnnotationMemberValue attributeValue = psiAnnotation.findDeclaredAttributeValue(parameter);
        if (null != attributeValue) {
            value = resolveElementValue(attributeValue, asClass);
        }
        return value;
    }

    @NotNull
    public static Collection<String> collectAnnotationsToCopy(@NotNull PsiField psiField, final Pattern... patterns) {
        Collection<String> annotationsToCopy = new ArrayList<String>();
        PsiModifierList modifierList = psiField.getModifierList();
        if (null != modifierList) {
            for (PsiAnnotation psiAnnotation : modifierList.getAnnotations()) {
                final String annotationName = getSimpleNameOf(psiAnnotation);
                for (Pattern pattern : patterns) {
                    if (pattern.matcher(annotationName).matches()) {
                        annotationsToCopy.add(psiAnnotation.getQualifiedName());
                    }
                }
            }
        }
        return annotationsToCopy;
    }

    @NotNull
    public static String buildAnnotationsString(@NotNull Collection<String> annotationsToCopy) {
        final StringBuilder builder = StringBuilderSpinAllocator.alloc();
        try {
            for (String annotationName : annotationsToCopy) {
                builder.append('@').append(annotationName).append(' ');
            }
            return builder.toString();
        } finally {
            StringBuilderSpinAllocator.dispose(builder);
        }
    }
}
