/**
 * Copyright 2012 the original author or authors.
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
package org.broadleafcommerce.core.inventory.exception;

/**
 * This is an exception that is thrown in case two threads try to concurrently modify inventory.
 * It is a friendlier business exception, thrown as a result of an OptimisticLockException.
 *
 * @author jfridye
 */
public class ConcurrentInventoryModificationException extends Exception {

    private static final long serialVersionUID = 1L;


    public ConcurrentInventoryModificationException() {
        super();
    }

    public ConcurrentInventoryModificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConcurrentInventoryModificationException(String message) {
        super(message);
    }

    public ConcurrentInventoryModificationException(Throwable cause) {
        super(cause);
    }

}
