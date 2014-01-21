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

package org.broadleafcommerce.common.exception;

/**
 * Exception thrown when a {@link EntityService service} method fails.
 * 
 * @author jfischer
 */
public class ServiceException extends Exception {
    
    private static final long serialVersionUID = -7084792578727995587L;
    
    // for serialization purposes
    protected ServiceException() {
        super();
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Checks to see if any of the causes of the chain of exceptions that led to this ServiceException are an instance
     * of the given class.
     * 
     * @param clazz
     * @return whether or not this exception's causes includes the given class.
     */
    public boolean containsCause(Class<? extends Throwable> clazz) {
        Throwable current = this;

        do {
            if (clazz.isAssignableFrom(current.getClass())) {
                return true;
            }
            current = current.getCause();
        } while (current.getCause() != null);

        return false;
    }

}
