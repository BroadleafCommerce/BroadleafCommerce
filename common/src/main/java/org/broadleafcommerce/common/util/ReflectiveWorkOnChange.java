/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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

import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.codehaus.jackson.map.util.LRUMap;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * This WorkOnChange implementation is for a narrow case where the work to be done is simply calling a method on a target object
 * and passing to that method the changed collection. Of course, there must be a method with the correct signature on
 * the target object. This implementation also uses caching to optimize repeat searches for the same method and minimize
 * reflection time.
 *
 * @author Jeff Fischer
 */
public class ReflectiveWorkOnChange implements WorkOnChange {

    private static Map<String, Method> methodCache = Collections.synchronizedMap(new LRUMap<String, Method>(10, 1000));

    private final Object target;
    private final String methodName;

    public ReflectiveWorkOnChange(Object target, String methodName) {
        this.target = target;
        this.methodName = methodName;
    }

    @Override
    public void doWork(Collection changed) {
        String key = target.getClass().getName() + "_" + changed.getClass().getName();
        Method method;
        if (methodCache.containsKey(key)) {
            method = methodCache.get(key);
        } else {
            method = searchForMethod(target.getClass(), changed);
            if (method != null) {
                methodCache.put(key, method);
            }
        }
        if (method == null) {
            throw new IllegalArgumentException("Unable to find the method (" + methodName + ") on the class (" + target.getClass().getName() + ")");
        }
        try {
            method.invoke(target, changed);
        } catch (IllegalAccessException e) {
            throw ExceptionHelper.refineException(e);
        } catch (InvocationTargetException e) {
            throw ExceptionHelper.refineException(e);
        }
    }

    protected Method searchForMethod(Class<?> targetClass, Object test) {
        Method method = ReflectionUtils.findMethod(target.getClass(), methodName, test.getClass());
        if (method == null) {
            Class[] interfaces = ClassUtils.getAllInterfaces(test);
            for (Class clazz : interfaces) {
                method = ReflectionUtils.findMethod(targetClass, methodName, clazz);
                if (method != null) {
                    break;
                }
            }
        }
        return method;
    }
}
