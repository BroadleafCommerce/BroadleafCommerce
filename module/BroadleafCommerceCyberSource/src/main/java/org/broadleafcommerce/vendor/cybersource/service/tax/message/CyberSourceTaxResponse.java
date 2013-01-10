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

import java.math.BigDecimal;

import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceResponse;
import org.broadleafcommerce.vendor.service.message.TaxResponse;

public class CyberSourceTaxResponse extends CyberSourceResponse implements TaxResponse {
        
    private static final long serialVersionUID = 1L;
    
    protected boolean isErrorDetected = false;
    protected String errorText;
    protected String merchantReferenceCode;
    protected String requestID;
    protected String decision;
    protected Integer reasonCode;
    protected String[] missingField;
    protected String[] invalidField;
    protected String requestToken;
    protected java.lang.String currency;
    protected Money grandTotalAmount;
    protected Money totalCityTaxAmount;
    protected java.lang.String city;
    protected Money totalCountyTaxAmount;
    protected java.lang.String county;
    protected Money totalDistrictTaxAmount;
    protected Money totalStateTaxAmount;
    protected java.lang.String state;
    protected Money totalTaxAmount;
    protected java.lang.String postalCode;
    protected java.lang.String geocode;
    protected CyberSourceTaxItemResponse[] itemResponses = new CyberSourceTaxItemResponse[]{};
    protected BigDecimal cityRate;
    protected BigDecimal stateRate;
    protected BigDecimal districtRate;
    protected BigDecimal countyRate;
    protected BigDecimal totalRate;
    
    public String getErrorCode() {
        throw new RuntimeException("ErrorCode not supported");
    }

    public String getErrorText() {
        return errorText;
    }

    public boolean isErrorDetected() {
        return isErrorDetected;
    }

    public void setErrorCode(String errorCode) {
        throw new RuntimeException("ErrorCode not supported");
    }

    public void setErrorDetected(boolean isErrorDetected) {
        this.isErrorDetected = isErrorDetected;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
    
    public String getMerchantReferenceCode() {
        return merchantReferenceCode;
    }

    public void setMerchantReferenceCode(String merchantReferenceCode) {
        this.merchantReferenceCode = merchantReferenceCode;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String[] getMissingField() {
        return missingField;
    }

    public void setMissingField(String[] missingField) {
        this.missingField = missingField;
    }

    public String[] getInvalidField() {
        return invalidField;
    }

    public void setInvalidField(String[] invalidField) {
        this.invalidField = invalidField;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public java.lang.String getCurrency() {
        return currency;
    }

    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }

    public Money getGrandTotalAmount() {
        return grandTotalAmount;
    }

    public void setGrandTotalAmount(Money grandTotalAmount) {
        this.grandTotalAmount = grandTotalAmount;
    }

    public Money getTotalCityTaxAmount() {
        return totalCityTaxAmount;
    }

    public void setTotalCityTaxAmount(Money totalCityTaxAmount) {
        this.totalCityTaxAmount = totalCityTaxAmount;
    }

    public java.lang.String getCity() {
        return city;
    }

    public void setCity(java.lang.String city) {
        this.city = city;
    }

    public Money getTotalCountyTaxAmount() {
        return totalCountyTaxAmount;
    }

    public void setTotalCountyTaxAmount(Money totalCountyTaxAmount) {
        this.totalCountyTaxAmount = totalCountyTaxAmount;
    }

    public java.lang.String getCounty() {
        return county;
    }

    public void setCounty(java.lang.String county) {
        this.county = county;
    }

    public Money getTotalDistrictTaxAmount() {
        return totalDistrictTaxAmount;
    }

    public void setTotalDistrictTaxAmount(Money totalDistrictTaxAmount) {
        this.totalDistrictTaxAmount = totalDistrictTaxAmount;
    }

    public Money getTotalStateTaxAmount() {
        return totalStateTaxAmount;
    }

    public void setTotalStateTaxAmount(Money totalStateTaxAmount) {
        this.totalStateTaxAmount = totalStateTaxAmount;
    }

    public java.lang.String getState() {
        return state;
    }

    public void setState(java.lang.String state) {
        this.state = state;
    }

    public Money getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(Money totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public java.lang.String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(java.lang.String postalCode) {
        this.postalCode = postalCode;
    }

    public java.lang.String getGeocode() {
        return geocode;
    }

    public void setGeocode(java.lang.String geocode) {
        this.geocode = geocode;
    }

    public CyberSourceTaxItemResponse[] getItemResponses() {
        return itemResponses;
    }

    public void setItemResponses(CyberSourceTaxItemResponse[] itemResponses) {
        this.itemResponses = itemResponses;
    }

    public BigDecimal getCityRate() {
        return cityRate;
    }

    public void setCityRate(BigDecimal cityRate) {
        this.cityRate = cityRate;
    }

    public BigDecimal getStateRate() {
        return stateRate;
    }

    public void setStateRate(BigDecimal stateRate) {
        this.stateRate = stateRate;
    }

    public BigDecimal getDistrictRate() {
        return districtRate;
    }

    public void setDistrictRate(BigDecimal districtRate) {
        this.districtRate = districtRate;
    }

    public BigDecimal getCountyRate() {
        return countyRate;
    }

    public void setCountyRate(BigDecimal countyRate) {
        this.countyRate = countyRate;
    }

    public BigDecimal getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(BigDecimal totalRate) {
        this.totalRate = totalRate;
    }

}
