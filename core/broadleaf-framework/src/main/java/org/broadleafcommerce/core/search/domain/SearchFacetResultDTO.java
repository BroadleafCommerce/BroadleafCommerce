/*
 * Copyright 2008-2013 the original author or authors.
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
