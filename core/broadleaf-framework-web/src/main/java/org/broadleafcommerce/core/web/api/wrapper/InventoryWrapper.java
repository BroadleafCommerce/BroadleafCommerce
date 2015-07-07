/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.catalog.domain.Sku;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "inventory")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class InventoryWrapper extends BaseWrapper {

    @XmlElement
    protected Long skuId;
    
    @XmlElement(nillable = true)
    protected Integer quantityAvailable;
    
    @XmlElement(nillable = true)
    protected String inventoryType;

    public void wrapDetails(Sku sku, Integer quantityAvailable, HttpServletRequest request) {
        if (sku != null) {
            this.skuId = sku.getId();
            if (sku.getInventoryType() != null) {
                this.inventoryType = sku.getInventoryType().getType();
            }
        }
        this.quantityAvailable = quantityAvailable;
    }

    public void wrapSummary(Sku sku, Integer quantity, HttpServletRequest request) {
        wrapDetails(sku, quantity, request);
    }
}
