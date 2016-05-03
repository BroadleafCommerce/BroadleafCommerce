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

import org.apache.commons.collections.Predicate;

import java.lang.reflect.ParameterizedType;


/**
 * A class that provides for a typed predicat
 * 
 * @author Andre Azzolini (apazzolini)
 *
 * @param <T> the type of object the predicate uses
 */
@SuppressWarnings("unchecked")
public abstract class TypedPredicate<T> implements Predicate {
    
    protected Class<T> clazz;
    
    public TypedPredicate() {
        clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    public boolean evaluate(Object value) {
        if (value == null || clazz.isAssignableFrom(value.getClass())) {
            return eval((T) value);
        }
        return false;
    }
    
    public abstract boolean eval(T value);

}
