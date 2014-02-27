/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.extension;

import org.apache.commons.beanutils.BeanComparator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The ExtensionManager pattern is intended for out of box components to be extended by Broadleaf modules.
 * 
 * Each component that needs an extension should define an interface which is a descendant of ExtensionHandler.
 * The concrete ExtensionManager class will utilize that interface as a parameter (e.g. T below).   
 * 
 * The default extension manager pattern loops through all handlers and examines their {@link ExtensionResultStatusType} 
 * to determine whether or not to continue with other handlers.
 * 
 * @author bpolster
 *
 * @param <T>
 */
public abstract class ExtensionManager<T extends ExtensionHandler> implements InvocationHandler {

    protected boolean handlersSorted = false;
    protected static String LOCK_OBJECT = new String("EM_LOCK");
    
    protected T extensionHandler;
    protected List<T> handlers = new ArrayList<T>();

    /**
     * Should take in a className that matches the ExtensionHandler interface being managed.
     * @param className
     */
    @SuppressWarnings("unchecked")
    public ExtensionManager(Class<T> _clazz) {
        extensionHandler = (T) Proxy.newProxyInstance(_clazz.getClassLoader(),
                new Class[] { _clazz },
                this);
    }
    
    public T getProxy() {
        return extensionHandler;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<T> getHandlers() {
        if (!handlersSorted) {
            synchronized (LOCK_OBJECT) {
                if (!handlersSorted) {
                    Comparator fieldCompare = new BeanComparator("priority");
                    Collections.sort(handlers, fieldCompare);
                    handlersSorted = true;
                }
            }
        }
        return handlers;
    }

    public void setHandlers(List<T> handlers) {
        this.handlers = handlers;
    }
    
    /**
     * Utility method that is useful for determining whether or not an ExtensionManager implementation
     * should continue after processing a ExtensionHandler call.
     * 
     * By default, returns true for CONTINUE
     * 
     * @return
     */
    public boolean shouldContinue(ExtensionResultStatusType result, ExtensionHandler handler,
            Method method, Object[] args) {
        if (result != null) {
            if (ExtensionResultStatusType.HANDLED_STOP.equals(result)) {
                return false;
            }
            
            if (ExtensionResultStatusType.HANDLED.equals(result) && ! continueOnHandled()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns whether or not this extension manager continues on {@link ExtensionResultStatusType}.HANDLED.   
     * 
     * @return
     */
    public boolean continueOnHandled() {
        return false;
    }

    /**
     * {@link ExtensionManager}s don't really need a priority but they pick up this property due to the 
     * fact that we want them to implement the same interface <T> as the handlers they are managing.   
     * 
     * @return
     */
    public int getPriority() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean notHandled = true;
        for (ExtensionHandler handler : getHandlers()) {
            try {
                ExtensionResultStatusType result = (ExtensionResultStatusType) method.invoke(handler, args);
                if (!ExtensionResultStatusType.NOT_HANDLED.equals(result)) {
                    notHandled = false;
                }
                if (!shouldContinue(result, handler, method, args)) {
                    break;
                }
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
        if (notHandled) {
            return ExtensionResultStatusType.NOT_HANDLED;
        } else {
            return ExtensionResultStatusType.HANDLED;
        }
    }
       
}
