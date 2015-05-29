/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
