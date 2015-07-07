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

package org.broadleafcommerce.core.inventory.service;


public class InventoryUnavailableException extends Exception {

    private static final long serialVersionUID = 1L;

    protected Long skuId;

    protected Integer quantityRequested;

    protected Integer quantityAvailable;

    public InventoryUnavailableException(String msg) {
        super(msg);
    }
    
    public InventoryUnavailableException(Long skuId, Integer quantityRequested, Integer quantityAvailable) {
        super();
        this.skuId = skuId;
        this.quantityAvailable = quantityAvailable;
        this.quantityRequested = quantityRequested;
    }

    public InventoryUnavailableException(String arg0, Long skuId, Integer quantityRequested, Integer quantityAvailable) {
        super(arg0);
        this.skuId = skuId;
        this.quantityAvailable = quantityAvailable;
        this.quantityRequested = quantityRequested;
    }
    
    public InventoryUnavailableException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public int getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(int quantityRequested) {
        this.quantityRequested = quantityRequested;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

}
