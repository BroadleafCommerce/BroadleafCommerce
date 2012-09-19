/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.order.service.exception;

import java.util.Map;

public class InventoryUnavailableException extends Exception {

    private static final long serialVersionUID = 1L;

    protected Map<Long, Integer> skuInventoryAvailable;

    public InventoryUnavailableException() {
        super();
    }

    public InventoryUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public InventoryUnavailableException(String message) {
        super(message);
    }

    public InventoryUnavailableException(Throwable cause) {
        super(cause);
    }

    public Map<Long, Integer> getSkuInventoryAvailable() {
        return skuInventoryAvailable;
    }

    public void setSkuInventoryAvailable(Map<Long, Integer> skuInventoryAvailable) {
        this.skuInventoryAvailable = skuInventoryAvailable;
    }

}
