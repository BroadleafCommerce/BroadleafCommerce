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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Enables basic inventory management on a Sku
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Embeddable
public class QuantityAvailableSkuTemplate {

    @Column(name = "QUANTITY_AVAILABLE")
    @AdminPresentation(friendlyName = "SkuImpl_Sku_QuantityAvailable",
            order = 1010,
            tab = ProductImpl.Presentation.Tab.Name.Inventory,
            tabOrder = ProductImpl.Presentation.Tab.Order.Inventory,
            group = ProductImpl.Presentation.Group.Name.Inventory,
            groupOrder = ProductImpl.Presentation.Group.Order.Inventory)
    protected Integer quantityAvailable = 0;

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }
    
    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }
    
}
