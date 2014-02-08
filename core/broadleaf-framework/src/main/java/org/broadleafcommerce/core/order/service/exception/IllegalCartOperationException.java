/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.service.exception;


public abstract class IllegalCartOperationException extends RuntimeException {

    private static final long serialVersionUID = 5113456015951023947L;

    protected IllegalCartOperationException() {
        super();
    }
    
    public IllegalCartOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public IllegalCartOperationException(String message) {
        super(message);
    }
    
    public IllegalCartOperationException(Throwable cause) {
        super(cause);
    }
    
    public abstract String getType();

}
