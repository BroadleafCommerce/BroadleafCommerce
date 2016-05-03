/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "productOptionAllowedValue")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ProductOptionValueWrapper extends BaseWrapper implements
        APIWrapper<ProductOptionValue> {
    
    @XmlElement
    protected String attributeValue;
    
    @XmlElement
    protected Money priceAdjustment;
    
    @XmlElement
    protected Long productOptionId;
    
    @Override
    public void wrapDetails(ProductOptionValue model, HttpServletRequest request) {
        this.attributeValue = model.getAttributeValue();
        this.priceAdjustment = model.getPriceAdjustment();
        this.productOptionId = model.getProductOption().getId();
    }

    @Override
    public void wrapSummary(ProductOptionValue model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the attributeValue
     */
    public String getAttributeValue() {
        return attributeValue;
    }

    
    /**
     * @param attributeValue the attributeValue to set
     */
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    
    /**
     * @return the priceAdjustment
     */
    public Money getPriceAdjustment() {
        return priceAdjustment;
    }

    
    /**
     * @param priceAdjustment the priceAdjustment to set
     */
    public void setPriceAdjustment(Money priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }

    
    /**
     * @return the productOptionId
     */
    public Long getProductOptionId() {
        return productOptionId;
    }

    
    /**
     * @param productOptionId the productOptionId to set
     */
    public void setProductOptionId(Long productOptionId) {
        this.productOptionId = productOptionId;
    }
}
