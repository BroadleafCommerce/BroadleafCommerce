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
