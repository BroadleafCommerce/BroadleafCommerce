/*
 * Copyright 2008-2009 the original author or authors.
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
package org.broadleafcommerce.vendor.cybersource.service.tax.message;

import org.broadleafcommerce.util.money.Money;

public class CyberSourceTaxItemResponse implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Money cityTaxAmount;
    private Money countyTaxAmount;
    private Money districtTaxAmount;
    private Money stateTaxAmount;
    private Money totalTaxAmount;
    private Long id;  // attribute

    /**
     * Gets the cityTaxAmount value for this TaxReplyItem.
     * 
     * @return cityTaxAmount
     */
    public Money getCityTaxAmount() {
        return cityTaxAmount;
    }


    /**
     * Sets the cityTaxAmount value for this TaxReplyItem.
     * 
     * @param cityTaxAmount
     */
    public void setCityTaxAmount(Money cityTaxAmount) {
        this.cityTaxAmount = cityTaxAmount;
    }


    /**
     * Gets the countyTaxAmount value for this TaxReplyItem.
     * 
     * @return countyTaxAmount
     */
    public Money getCountyTaxAmount() {
        return countyTaxAmount;
    }


    /**
     * Sets the countyTaxAmount value for this TaxReplyItem.
     * 
     * @param countyTaxAmount
     */
    public void setCountyTaxAmount(Money countyTaxAmount) {
        this.countyTaxAmount = countyTaxAmount;
    }


    /**
     * Gets the districtTaxAmount value for this TaxReplyItem.
     * 
     * @return districtTaxAmount
     */
    public Money getDistrictTaxAmount() {
        return districtTaxAmount;
    }


    /**
     * Sets the districtTaxAmount value for this TaxReplyItem.
     * 
     * @param districtTaxAmount
     */
    public void setDistrictTaxAmount(Money districtTaxAmount) {
        this.districtTaxAmount = districtTaxAmount;
    }


    /**
     * Gets the stateTaxAmount value for this TaxReplyItem.
     * 
     * @return stateTaxAmount
     */
    public Money getStateTaxAmount() {
        return stateTaxAmount;
    }


    /**
     * Sets the stateTaxAmount value for this TaxReplyItem.
     * 
     * @param stateTaxAmount
     */
    public void setStateTaxAmount(Money stateTaxAmount) {
        this.stateTaxAmount = stateTaxAmount;
    }


    /**
     * Gets the totalTaxAmount value for this TaxReplyItem.
     * 
     * @return totalTaxAmount
     */
    public Money getTotalTaxAmount() {
        return totalTaxAmount;
    }


    /**
     * Sets the totalTaxAmount value for this TaxReplyItem.
     * 
     * @param totalTaxAmount
     */
    public void setTotalTaxAmount(Money totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }


    /**
     * Gets the id value for this TaxReplyItem.
     * 
     * @return id
     */
    public Long getId() {
        return id;
    }


    /**
     * Sets the id value for this TaxReplyItem.
     * 
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

}
