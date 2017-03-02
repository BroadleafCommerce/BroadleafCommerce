/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.exception;

/**
 * Created by Jon on 6/24/15.
 */
public class EntityNotFoundException extends IllegalArgumentException {
    /**
     * Constructs an <code>EntityNotFoundException</code> with no
     * detail message.
     */
    public EntityNotFoundException() {
        super();
    }

    /**
     * Constructs an <code>EntityNotFoundException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public EntityNotFoundException(String s) {
        super(s);
    }
}
