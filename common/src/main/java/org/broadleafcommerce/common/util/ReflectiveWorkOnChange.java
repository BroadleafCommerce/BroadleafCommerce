/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
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

    private static Map<String, Method> methodCache = new EfficientLRUMap<String, Method>(1000);

    private final Object target;
    private final String methodName;

    public ReflectiveWorkOnChange(Object target, String methodName) {
        this.target = target;
        this.methodName = methodName;
    }

    @Override
    public void doWork(Collection changed) {
        String key = target.getClass().getName() + "." + methodName + "(" + changed.getClass().getName() + ")";
        Method method = methodCache.get(key);
        if (method == null) {
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
