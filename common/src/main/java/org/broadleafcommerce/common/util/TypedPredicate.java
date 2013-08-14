/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        if (clazz.isAssignableFrom(value.getClass())) {
            return eval((T) value);
        }
        return false;
    }
    
    public abstract boolean eval(T value);

}
