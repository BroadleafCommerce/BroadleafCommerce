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
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceItemRequest;

public class CyberSourceTaxItemRequest extends CyberSourceItemRequest {

    private static final long serialVersionUID = 1L;

    private java.lang.String productCode;
    private java.lang.String productName;
    private java.lang.String productSKU;
    private java.lang.String productRisk;
    private Money taxAmount;
    private Money cityOverrideAmount;
    private Double cityOverrideRate;
    private Money countyOverrideAmount;
    private Double countyOverrideRate;
    private Money districtOverrideAmount;
    private Double districtOverrideRate;
    private Money stateOverrideAmount;
    private Double stateOverrideRate;
    private Money countryOverrideAmount;
    private Double countryOverrideRate;
    private java.lang.String orderAcceptanceCity;
    private java.lang.String orderAcceptanceCounty;
    private java.lang.String orderAcceptanceCountry;
    private java.lang.String orderAcceptanceState;
    private java.lang.String orderAcceptancePostalCode;
    private java.lang.String orderOriginCity;
    private java.lang.String orderOriginCounty;
    private java.lang.String orderOriginCountry;
    private java.lang.String orderOriginState;
    private java.lang.String orderOriginPostalCode;
    private java.lang.String shipFromCity;
    private java.lang.String shipFromCounty;
    private java.lang.String shipFromCountry;
    private java.lang.String shipFromState;
    private java.lang.String shipFromPostalCode;
    private java.lang.String export;
    private java.lang.String noExport;
    private Money nationalTax;
    private Double vatRate;
    private java.lang.String sellerRegistration;
    private java.lang.String buyerRegistration;
    private java.lang.String middlemanRegistration;
    private java.lang.String pointOfTitleTransfer;
    private java.lang.String giftCategory;
    private java.lang.String timeCategory;
    private java.lang.String hostHedge;
    private java.lang.String timeHedge;
    private java.lang.String velocityHedge;
    private java.lang.String nonsensicalHedge;
    private java.lang.String phoneHedge;
    private java.lang.String obscenitiesHedge;
    private java.lang.String unitOfMeasure;
    private Double taxRate;
    private Money totalAmount;
    private Money discountAmount;
    private Double discountRate;
    private java.lang.String commodityCode;
    private java.lang.String grossNetIndicator;
    private java.lang.String taxTypeApplied;
    private java.lang.String discountIndicator;
    private java.lang.String alternateTaxID;
    private Money alternateTaxAmount;
    private Money alternateTaxTypeApplied;
    private Double alternateTaxRate;
    private java.lang.String alternateTaxType;
    private Money localTax;
    private java.lang.String zeroCostToCustomerIndicator;
    private Long nonCyberSourceQuantity;
    private Long nonCyberSourceFulfillmentGroupId;
    
    public java.lang.String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(java.lang.String productCode) {
        this.productCode = productCode;
    }
    
    public java.lang.String getProductName() {
        return productName;
    }
    
    public void setProductName(java.lang.String productName) {
        this.productName = productName;
    }
    
    public java.lang.String getProductSKU() {
        return productSKU;
    }
    
    public void setProductSKU(java.lang.String productSKU) {
        this.productSKU = productSKU;
    }
    
    public java.lang.String getProductRisk() {
        return productRisk;
    }
    
    public void setProductRisk(java.lang.String productRisk) {
        this.productRisk = productRisk;
    }
    
    public Money getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(Money taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public Money getCityOverrideAmount() {
        return cityOverrideAmount;
    }
    
    public void setCityOverrideAmount(Money cityOverrideAmount) {
        this.cityOverrideAmount = cityOverrideAmount;
    }
    
    public Double getCityOverrideRate() {
        return cityOverrideRate;
    }
    
    public void setCityOverrideRate(Double cityOverrideRate) {
        this.cityOverrideRate = cityOverrideRate;
    }
    
    public Money getCountyOverrideAmount() {
        return countyOverrideAmount;
    }
    
    public void setCountyOverrideAmount(Money countyOverrideAmount) {
        this.countyOverrideAmount = countyOverrideAmount;
    }
    
    public Double getCountyOverrideRate() {
        return countyOverrideRate;
    }
    
    public void setCountyOverrideRate(Double countyOverrideRate) {
        this.countyOverrideRate = countyOverrideRate;
    }
    
    public Money getDistrictOverrideAmount() {
        return districtOverrideAmount;
    }
    
    public void setDistrictOverrideAmount(Money districtOverrideAmount) {
        this.districtOverrideAmount = districtOverrideAmount;
    }
    
    public Double getDistrictOverrideRate() {
        return districtOverrideRate;
    }
    
    public void setDistrictOverrideRate(Double districtOverrideRate) {
        this.districtOverrideRate = districtOverrideRate;
    }
    
    public Money getStateOverrideAmount() {
        return stateOverrideAmount;
    }
    
    public void setStateOverrideAmount(Money stateOverrideAmount) {
        this.stateOverrideAmount = stateOverrideAmount;
    }
    
    public Double getStateOverrideRate() {
        return stateOverrideRate;
    }
    
    public void setStateOverrideRate(Double stateOverrideRate) {
        this.stateOverrideRate = stateOverrideRate;
    }
    
    public Money getCountryOverrideAmount() {
        return countryOverrideAmount;
    }
    
    public void setCountryOverrideAmount(Money countryOverrideAmount) {
        this.countryOverrideAmount = countryOverrideAmount;
    }
    
    public Double getCountryOverrideRate() {
        return countryOverrideRate;
    }
    
    public void setCountryOverrideRate(Double countryOverrideRate) {
        this.countryOverrideRate = countryOverrideRate;
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
    
    public java.lang.String getShipFromCity() {
        return shipFromCity;
    }
    
    public void setShipFromCity(java.lang.String shipFromCity) {
        this.shipFromCity = shipFromCity;
    }
    
    public java.lang.String getShipFromCounty() {
        return shipFromCounty;
    }
    
    public void setShipFromCounty(java.lang.String shipFromCounty) {
        this.shipFromCounty = shipFromCounty;
    }
    
    public java.lang.String getShipFromCountry() {
        return shipFromCountry;
    }
    
    public void setShipFromCountry(java.lang.String shipFromCountry) {
        this.shipFromCountry = shipFromCountry;
    }
    
    public java.lang.String getShipFromState() {
        return shipFromState;
    }
    
    public void setShipFromState(java.lang.String shipFromState) {
        this.shipFromState = shipFromState;
    }
    
    public java.lang.String getShipFromPostalCode() {
        return shipFromPostalCode;
    }
    
    public void setShipFromPostalCode(java.lang.String shipFromPostalCode) {
        this.shipFromPostalCode = shipFromPostalCode;
    }
    
    public java.lang.String getExport() {
        return export;
    }
    
    public void setExport(java.lang.String export) {
        this.export = export;
    }
    
    public java.lang.String getNoExport() {
        return noExport;
    }
    
    public void setNoExport(java.lang.String noExport) {
        this.noExport = noExport;
    }
    
    public Money getNationalTax() {
        return nationalTax;
    }
    
    public void setNationalTax(Money nationalTax) {
        this.nationalTax = nationalTax;
    }
    
    public Double getVatRate() {
        return vatRate;
    }
    
    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
    }
    
    public java.lang.String getSellerRegistration() {
        return sellerRegistration;
    }
    
    public void setSellerRegistration(java.lang.String sellerRegistration) {
        this.sellerRegistration = sellerRegistration;
    }
    
    public java.lang.String getBuyerRegistration() {
        return buyerRegistration;
    }
    
    public void setBuyerRegistration(java.lang.String buyerRegistration) {
        this.buyerRegistration = buyerRegistration;
    }
    
    public java.lang.String getMiddlemanRegistration() {
        return middlemanRegistration;
    }
    
    public void setMiddlemanRegistration(java.lang.String middlemanRegistration) {
        this.middlemanRegistration = middlemanRegistration;
    }
    
    public java.lang.String getPointOfTitleTransfer() {
        return pointOfTitleTransfer;
    }
    
    public void setPointOfTitleTransfer(java.lang.String pointOfTitleTransfer) {
        this.pointOfTitleTransfer = pointOfTitleTransfer;
    }
    
    public java.lang.String getGiftCategory() {
        return giftCategory;
    }
    
    public void setGiftCategory(java.lang.String giftCategory) {
        this.giftCategory = giftCategory;
    }
    
    public java.lang.String getTimeCategory() {
        return timeCategory;
    }
    
    public void setTimeCategory(java.lang.String timeCategory) {
        this.timeCategory = timeCategory;
    }
    
    public java.lang.String getHostHedge() {
        return hostHedge;
    }
    
    public void setHostHedge(java.lang.String hostHedge) {
        this.hostHedge = hostHedge;
    }
    
    public java.lang.String getTimeHedge() {
        return timeHedge;
    }
    
    public void setTimeHedge(java.lang.String timeHedge) {
        this.timeHedge = timeHedge;
    }
    
    public java.lang.String getVelocityHedge() {
        return velocityHedge;
    }
    
    public void setVelocityHedge(java.lang.String velocityHedge) {
        this.velocityHedge = velocityHedge;
    }
    
    public java.lang.String getNonsensicalHedge() {
        return nonsensicalHedge;
    }
    
    public void setNonsensicalHedge(java.lang.String nonsensicalHedge) {
        this.nonsensicalHedge = nonsensicalHedge;
    }
    
    public java.lang.String getPhoneHedge() {
        return phoneHedge;
    }
    
    public void setPhoneHedge(java.lang.String phoneHedge) {
        this.phoneHedge = phoneHedge;
    }
    
    public java.lang.String getObscenitiesHedge() {
        return obscenitiesHedge;
    }
    
    public void setObscenitiesHedge(java.lang.String obscenitiesHedge) {
        this.obscenitiesHedge = obscenitiesHedge;
    }
    
    public java.lang.String getUnitOfMeasure() {
        return unitOfMeasure;
    }
    
    public void setUnitOfMeasure(java.lang.String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
    
    public Double getTaxRate() {
        return taxRate;
    }
    
    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }
    
    public Money getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Money getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(Money discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public Double getDiscountRate() {
        return discountRate;
    }
    
    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }
    
    public java.lang.String getCommodityCode() {
        return commodityCode;
    }
    
    public void setCommodityCode(java.lang.String commodityCode) {
        this.commodityCode = commodityCode;
    }
    
    public java.lang.String getGrossNetIndicator() {
        return grossNetIndicator;
    }
    
    public void setGrossNetIndicator(java.lang.String grossNetIndicator) {
        this.grossNetIndicator = grossNetIndicator;
    }
    
    public java.lang.String getTaxTypeApplied() {
        return taxTypeApplied;
    }
    
    public void setTaxTypeApplied(java.lang.String taxTypeApplied) {
        this.taxTypeApplied = taxTypeApplied;
    }
    
    public java.lang.String getDiscountIndicator() {
        return discountIndicator;
    }
    
    public void setDiscountIndicator(java.lang.String discountIndicator) {
        this.discountIndicator = discountIndicator;
    }
    
    public java.lang.String getAlternateTaxID() {
        return alternateTaxID;
    }
    
    public void setAlternateTaxID(java.lang.String alternateTaxID) {
        this.alternateTaxID = alternateTaxID;
    }
    
    public Money getAlternateTaxAmount() {
        return alternateTaxAmount;
    }
    
    public void setAlternateTaxAmount(Money alternateTaxAmount) {
        this.alternateTaxAmount = alternateTaxAmount;
    }
    
    public Money getAlternateTaxTypeApplied() {
        return alternateTaxTypeApplied;
    }
    
    public void setAlternateTaxTypeApplied(Money alternateTaxTypeApplied) {
        this.alternateTaxTypeApplied = alternateTaxTypeApplied;
    }
    
    public Double getAlternateTaxRate() {
        return alternateTaxRate;
    }
    
    public void setAlternateTaxRate(Double alternateTaxRate) {
        this.alternateTaxRate = alternateTaxRate;
    }
    
    public java.lang.String getAlternateTaxType() {
        return alternateTaxType;
    }
    
    public void setAlternateTaxType(java.lang.String alternateTaxType) {
        this.alternateTaxType = alternateTaxType;
    }
    
    public Money getLocalTax() {
        return localTax;
    }
    
    public void setLocalTax(Money localTax) {
        this.localTax = localTax;
    }
    
    public java.lang.String getZeroCostToCustomerIndicator() {
        return zeroCostToCustomerIndicator;
    }
    
    public void setZeroCostToCustomerIndicator(java.lang.String zeroCostToCustomerIndicator) {
        this.zeroCostToCustomerIndicator = zeroCostToCustomerIndicator;
    }

    public Long getNonCyberSourceQuantity() {
        return nonCyberSourceQuantity;
    }

    public void setNonCyberSourceQuantity(Long nonCyberSourceQuantity) {
        this.nonCyberSourceQuantity = nonCyberSourceQuantity;
    }

    public Long getNonCyberSourceFulfillmentGroupId() {
        return nonCyberSourceFulfillmentGroupId;
    }

    public void setNonCyberSourceFulfillmentGroupId(Long nonCyberSourceFulfillmentGroupId) {
        this.nonCyberSourceFulfillmentGroupId = nonCyberSourceFulfillmentGroupId;
    }
    
}
