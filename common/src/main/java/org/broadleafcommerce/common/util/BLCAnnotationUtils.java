/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
