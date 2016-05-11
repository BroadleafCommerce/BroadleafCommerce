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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Invocation handler for unit testing byte-weaved classes. Use this InvocationHandler and utility method when Spring is unavailable
 * to complete byte-weaving.
 * 
 * @author Joshua Skorton (jskorton)
 */
public class InvocationHandlerForUnitTestingByteWeavedClasses implements InvocationHandler {

    /**
     * This utility method will return a Proxy of a chosen type that response to an array of chose Interfaces and uses a
     * InvocationHandlerForUnitTestingByteWeavedClasses that is backed by an array of chosen Objects.
     * 
     * @param proxyType
     * @param interfaces
     * @param objectsForByteWeaving
     * @return
     */
    public static <T> T createProxy(Class<T> proxyType, Class<?>[] interfaces, Object[] objectsForByteWeaving) {
        InvocationHandler handler = new InvocationHandlerForUnitTestingByteWeavedClasses(objectsForByteWeaving);
        return (T) Proxy.newProxyInstance(handler.getClass().getClassLoader(), interfaces, handler);
    }

    protected Object[] objectsForByteWeaving;

    public InvocationHandlerForUnitTestingByteWeavedClasses(Object[] objectsForByteWeaving) {
        this.objectsForByteWeaving = objectsForByteWeaving;
    }

    /**
     * Will invoke a chosen method against an array of Objects that are meant to be byte-weaved together.  Invoke will return when
     * the first object is found that can be successfully used with the chosen method.  If no objects are found to work with
     * the chosen method, null will be returned.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        for (Object object : objectsForByteWeaving) {
            try {
                return method.invoke(object, args);
            } catch (IllegalArgumentException exception) {
                continue;
            }
        }

        return null;
    }
    
    /**
     * Returns an array of Objects that are meant to be byte-weaved.
     * 
     * @return
     */
    public Object[] getObjectsForByteWeaving() {
        return objectsForByteWeaving;
    }

    /**
     * Sets an array of Objects that are meant to be byte-weaved.
     * 
     * @param objects
     */
    public void setObjectsForByteWeaving(Object[] objects) {
        this.objectsForByteWeaving = objects;
    }

}
