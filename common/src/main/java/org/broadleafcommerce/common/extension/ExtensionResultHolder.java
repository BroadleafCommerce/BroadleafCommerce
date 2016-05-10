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
package org.broadleafcommerce.common.extension;

import java.util.HashMap;
import java.util.Map;

/**
 * If a service extension using the {@link ExtensionManager} pattern expects a result from the extension, it should
 * pass in an instance of this class into the method call.   
 * 
 * The extension points can examine or update this class with response information and set a single return value with
 * {@link #setResult(Object)} or add values via the contextMap provided with {@link #getContextMap()}
 * 
 * @author bpolster
 *
 */
public class ExtensionResultHolder<T> {

    protected T result;
    protected Throwable throwable;
    protected Map<String, Object> contextMap = new HashMap<String, Object>();

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Map<String, Object> getContextMap() {
        return contextMap;
    }
}
