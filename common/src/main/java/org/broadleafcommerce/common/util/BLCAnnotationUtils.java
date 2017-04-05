/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.util;

import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;

/**
 * <p>
 * Spring has a much more elaborate and full-featured version of this within its {@link AnnotationUtils}. Consider
 * using that instead of relying on this class.
 * 
 * <p>
 * Convenience methods for interacting with annotations.
 * 
 * @author Chris Kittrell
 * @deprecated use Spring's {@link AnnotationUtils} instead.
 */
@Deprecated
public class BLCAnnotationUtils {
    
    /**
     * <p>
     * Rather than using this method, consider using Spring's {@link AnnotationUtils} which also includes support for
     * composed, meta-annotations and synthesizing annotations with {@link AliasFor}.
     * 
     * <p>
     * Given an array and a typed predicate, determines if the array has an object that matches the condition of the
     * predicate. The predicate should evaluate to true when a match occurs.
     * 
     * @param annotationClass
     * @param clazz
     * @return the annotation of annotationClass if it can be found
     * @deprecated use SPring's {@link AnnotationUtils} instead
     */
    @Deprecated
    public static <A extends Annotation> A getAnnotationFromClassOrInterface(Class<A> annotationClass, Class clazz) {
        Annotation result = clazz.getAnnotation(annotationClass);
        if (result == null) {
            for (Class inter : clazz.getInterfaces()) {
                result = inter.getAnnotation(annotationClass);
                if (result != null) {
                    break;
                }
            }
        }
        return (A) result;
    }

}
