/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
