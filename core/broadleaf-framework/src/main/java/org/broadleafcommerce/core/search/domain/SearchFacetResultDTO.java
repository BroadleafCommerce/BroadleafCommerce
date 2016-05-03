/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.domain;

import java.math.BigDecimal;

/**
 * @author Andre Azzolini (apazzolini)
 */
public class SearchFacetResultDTO {
    
    protected SearchFacet facet;
    
    protected String value;
    
    protected BigDecimal minValue;
    protected BigDecimal maxValue;
    
    protected Integer quantity;
    
    protected boolean active;
    
    public SearchFacet getFacet() {
        return facet;
    }

    public void setFacet(SearchFacet facet) {
        this.facet = facet;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getValueKey() {
        String value = getValue();
        
        if (value == null) {
            value = "range[" + getMinValue() + ":" + getMaxValue() + "]";
        }
        
        return value;
    }
    
}
