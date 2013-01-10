package org.broadleafcommerce.vendor.cybersource.service.tax.message;

import java.util.List;

import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.cybersource.service.message.AutoNumberMemberIdList;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceBillingRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceItemRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceServiceType;

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
public class CyberSourceTaxRequest extends CyberSourceRequest {

private static final long serialVersionUID = 1L;
    
    protected String currency;
    protected List<CyberSourceItemRequest> itemRequests = new AutoNumberMemberIdList();
    protected CyberSourceBillingRequest billingRequest;
    protected Money grandTotal;
    protected Boolean useGrandTotal = Boolean.FALSE;
    protected java.lang.String nexus;
    protected java.lang.String noNexus;
    protected java.lang.String orderAcceptanceCity;
    protected java.lang.String orderAcceptanceCounty;
    protected java.lang.String orderAcceptanceCountry;
    protected java.lang.String orderAcceptanceState;
    protected java.lang.String orderAcceptancePostalCode;
    protected java.lang.String orderOriginCity;
    protected java.lang.String orderOriginCounty;
    protected java.lang.String orderOriginCountry;
    protected java.lang.String orderOriginState;
    protected java.lang.String orderOriginPostalCode;
    
    public CyberSourceTaxRequest() {
        super(CyberSourceServiceType.TAX);
    }

    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public List<CyberSourceItemRequest> getItemRequests() {
        return itemRequests;
    }
    
    public Money getGrandTotal() {
        return grandTotal;
    }
    
    public void setGrandTotal(Money grandTotal) {
        this.grandTotal = grandTotal;
    }
    
    public Boolean getUseGrandTotal() {
        return useGrandTotal;
    }
    
    public void setUseGrandTotal(Boolean useGrandTotal) {
        this.useGrandTotal = useGrandTotal;
    }
    
    public java.lang.String getNexus() {
        return nexus;
    }
    
    public void setNexus(java.lang.String nexus) {
        this.nexus = nexus;
    }
    
    public java.lang.String getNoNexus() {
        return noNexus;
    }
    
    public void setNoNexus(java.lang.String noNexus) {
        this.noNexus = noNexus;
    }
    
    public java.lang.String getOrderAcceptanceCity() {
        return orderAcceptanceCity;
    }
    
    public void setOrderAcceptanceCity(java.lang.String orderAcceptanceCity) {
        this.orderAcceptanceCity = orderAcceptanceCity;
    }
    
    public java.lang.String getOrderAcceptanceCounty() {
        return orderAcceptanceCounty;
    }
    
    public void setOrderAcceptanceCounty(java.lang.String orderAcceptanceCounty) {
        this.orderAcceptanceCounty = orderAcceptanceCounty;
    }
    
    public java.lang.String getOrderAcceptanceCountry() {
        return orderAcceptanceCountry;
    }
    
    public void setOrderAcceptanceCountry(java.lang.String orderAcceptanceCountry) {
        this.orderAcceptanceCountry = orderAcceptanceCountry;
    }
    
    public java.lang.String getOrderAcceptanceState() {
        return orderAcceptanceState;
    }
    
    public void setOrderAcceptanceState(java.lang.String orderAcceptanceState) {
        this.orderAcceptanceState = orderAcceptanceState;
    }
    
    public java.lang.String getOrderAcceptancePostalCode() {
        return orderAcceptancePostalCode;
    }
    
    public void setOrderAcceptancePostalCode(java.lang.String orderAcceptancePostalCode) {
        this.orderAcceptancePostalCode = orderAcceptancePostalCode;
    }
    
    public java.lang.String getOrderOriginCity() {
        return orderOriginCity;
    }
    
    public void setOrderOriginCity(java.lang.String orderOriginCity) {
        this.orderOriginCity = orderOriginCity;
    }
    
    public java.lang.String getOrderOriginCounty() {
        return orderOriginCounty;
    }
    
    public void setOrderOriginCounty(java.lang.String orderOriginCounty) {
        this.orderOriginCounty = orderOriginCounty;
    }
    
    public java.lang.String getOrderOriginCountry() {
        return orderOriginCountry;
    }
    
    public void setOrderOriginCountry(java.lang.String orderOriginCountry) {
        this.orderOriginCountry = orderOriginCountry;
    }
    
    public java.lang.String getOrderOriginState() {
        return orderOriginState;
    }
    
    public void setOrderOriginState(java.lang.String orderOriginState) {
        this.orderOriginState = orderOriginState;
    }
    
    public java.lang.String getOrderOriginPostalCode() {
        return orderOriginPostalCode;
    }
    
    public void setOrderOriginPostalCode(java.lang.String orderOriginPostalCode) {
        this.orderOriginPostalCode = orderOriginPostalCode;
    }

    public CyberSourceBillingRequest getBillingRequest() {
        return billingRequest;
    }
    
    public void setBillingRequest(CyberSourceBillingRequest billingRequest) {
        this.billingRequest = billingRequest;
    }
    
    public int cacheKey() {
        /*
         * Postal code alone is not specific enough for accurate geocode / tax jurisdiction
         * determination. For example, the same postal code can sometimes span more than
         * one city or county, especially in rural areas.
         */
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((orderAcceptancePostalCode == null) ? 0 : orderAcceptancePostalCode.hashCode());
        result = prime * result + ((orderAcceptanceCity == null) ? 0 : orderAcceptanceCity.hashCode());
        result = prime * result + ((orderAcceptanceCounty == null) ? 0 : orderAcceptanceCounty.hashCode());
        result = prime * result + ((orderAcceptanceState == null) ? 0 : orderAcceptanceState.hashCode());
        result = prime * result + ((orderAcceptanceCountry == null) ? 0 : orderAcceptanceCountry.hashCode());
        result = prime * result + ((billingRequest == null || billingRequest.getPostalCode() == null) ? 0 : billingRequest.getPostalCode().hashCode());
        result = prime * result + ((billingRequest == null || billingRequest.getCity() == null) ? 0 : billingRequest.getCity().hashCode());
        result = prime * result + ((billingRequest == null || billingRequest.getCounty() == null) ? 0 : billingRequest.getCounty().hashCode());
        result = prime * result + ((billingRequest == null || billingRequest.getState() == null) ? 0 : billingRequest.getState().hashCode());
        result = prime * result + ((billingRequest == null || billingRequest.getCountry() == null) ? 0 : billingRequest.getCountry().hashCode());
        return result;
    }
}
