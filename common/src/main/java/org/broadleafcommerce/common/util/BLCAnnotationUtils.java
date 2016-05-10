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

import java.lang.annotation.Annotation;

/**
 * Convenience methods for interacting with annotations
 * 
 * @author Chris Kittrell
 */
public class BLCAnnotationUtils {
    
    /**
     * Given an array and a typed predicate, determines if the array has an object that matches the condition of the
     * predicate. The predicate should evaluate to true when a match occurs.
     * 
     * @param annotationType
     * @param entity
     * @return the annotation of annotationType if it can be found
     */
    public static Annotation getAnnotationFromEntityOrInterface(Class annotationType, Class entity) {
        Annotation result = entity.getAnnotation(annotationType);
        if (result == null) {
            for (Class inter : entity.getInterfaces()) {
                result = inter.getAnnotation(annotationType);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

}
