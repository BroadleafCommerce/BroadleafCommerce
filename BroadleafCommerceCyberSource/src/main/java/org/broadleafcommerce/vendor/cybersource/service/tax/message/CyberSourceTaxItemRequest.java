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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((alternateTaxAmount == null) ? 0 : alternateTaxAmount.hashCode());
		result = prime * result + ((alternateTaxID == null) ? 0 : alternateTaxID.hashCode());
		result = prime * result + ((alternateTaxRate == null) ? 0 : alternateTaxRate.hashCode());
		result = prime * result + ((alternateTaxType == null) ? 0 : alternateTaxType.hashCode());
		result = prime * result + ((alternateTaxTypeApplied == null) ? 0 : alternateTaxTypeApplied.hashCode());
		result = prime * result + ((buyerRegistration == null) ? 0 : buyerRegistration.hashCode());
		result = prime * result + ((cityOverrideAmount == null) ? 0 : cityOverrideAmount.hashCode());
		result = prime * result + ((cityOverrideRate == null) ? 0 : cityOverrideRate.hashCode());
		result = prime * result + ((commodityCode == null) ? 0 : commodityCode.hashCode());
		result = prime * result + ((countryOverrideAmount == null) ? 0 : countryOverrideAmount.hashCode());
		result = prime * result + ((countryOverrideRate == null) ? 0 : countryOverrideRate.hashCode());
		result = prime * result + ((countyOverrideAmount == null) ? 0 : countyOverrideAmount.hashCode());
		result = prime * result + ((countyOverrideRate == null) ? 0 : countyOverrideRate.hashCode());
		result = prime * result + ((discountAmount == null) ? 0 : discountAmount.hashCode());
		result = prime * result + ((discountIndicator == null) ? 0 : discountIndicator.hashCode());
		result = prime * result + ((discountRate == null) ? 0 : discountRate.hashCode());
		result = prime * result + ((districtOverrideAmount == null) ? 0 : districtOverrideAmount.hashCode());
		result = prime * result + ((districtOverrideRate == null) ? 0 : districtOverrideRate.hashCode());
		result = prime * result + ((export == null) ? 0 : export.hashCode());
		result = prime * result + ((giftCategory == null) ? 0 : giftCategory.hashCode());
		result = prime * result + ((grossNetIndicator == null) ? 0 : grossNetIndicator.hashCode());
		result = prime * result + ((hostHedge == null) ? 0 : hostHedge.hashCode());
		result = prime * result + ((localTax == null) ? 0 : localTax.hashCode());
		result = prime * result + ((middlemanRegistration == null) ? 0 : middlemanRegistration.hashCode());
		result = prime * result + ((nationalTax == null) ? 0 : nationalTax.hashCode());
		result = prime * result + ((noExport == null) ? 0 : noExport.hashCode());
		result = prime * result + ((nonsensicalHedge == null) ? 0 : nonsensicalHedge.hashCode());
		result = prime * result + ((obscenitiesHedge == null) ? 0 : obscenitiesHedge.hashCode());
		result = prime * result + ((orderAcceptanceCity == null) ? 0 : orderAcceptanceCity.hashCode());
		result = prime * result + ((orderAcceptanceCountry == null) ? 0 : orderAcceptanceCountry.hashCode());
		result = prime * result + ((orderAcceptanceCounty == null) ? 0 : orderAcceptanceCounty.hashCode());
		result = prime * result + ((orderAcceptancePostalCode == null) ? 0 : orderAcceptancePostalCode.hashCode());
		result = prime * result + ((orderAcceptanceState == null) ? 0 : orderAcceptanceState.hashCode());
		result = prime * result + ((orderOriginCity == null) ? 0 : orderOriginCity.hashCode());
		result = prime * result + ((orderOriginCountry == null) ? 0 : orderOriginCountry.hashCode());
		result = prime * result + ((orderOriginCounty == null) ? 0 : orderOriginCounty.hashCode());
		result = prime * result + ((orderOriginPostalCode == null) ? 0 : orderOriginPostalCode.hashCode());
		result = prime * result + ((orderOriginState == null) ? 0 : orderOriginState.hashCode());
		result = prime * result + ((phoneHedge == null) ? 0 : phoneHedge.hashCode());
		result = prime * result + ((pointOfTitleTransfer == null) ? 0 : pointOfTitleTransfer.hashCode());
		result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		result = prime * result + ((productRisk == null) ? 0 : productRisk.hashCode());
		result = prime * result + ((productSKU == null) ? 0 : productSKU.hashCode());
		result = prime * result + ((sellerRegistration == null) ? 0 : sellerRegistration.hashCode());
		result = prime * result + ((shipFromCity == null) ? 0 : shipFromCity.hashCode());
		result = prime * result + ((shipFromCountry == null) ? 0 : shipFromCountry.hashCode());
		result = prime * result + ((shipFromCounty == null) ? 0 : shipFromCounty.hashCode());
		result = prime * result + ((shipFromPostalCode == null) ? 0 : shipFromPostalCode.hashCode());
		result = prime * result + ((shipFromState == null) ? 0 : shipFromState.hashCode());
		result = prime * result + ((stateOverrideAmount == null) ? 0 : stateOverrideAmount.hashCode());
		result = prime * result + ((stateOverrideRate == null) ? 0 : stateOverrideRate.hashCode());
		result = prime * result + ((taxAmount == null) ? 0 : taxAmount.hashCode());
		result = prime * result + ((taxRate == null) ? 0 : taxRate.hashCode());
		result = prime * result + ((taxTypeApplied == null) ? 0 : taxTypeApplied.hashCode());
		result = prime * result + ((timeCategory == null) ? 0 : timeCategory.hashCode());
		result = prime * result + ((timeHedge == null) ? 0 : timeHedge.hashCode());
		result = prime * result + ((totalAmount == null) ? 0 : totalAmount.hashCode());
		result = prime * result + ((unitOfMeasure == null) ? 0 : unitOfMeasure.hashCode());
		result = prime * result + ((vatRate == null) ? 0 : vatRate.hashCode());
		result = prime * result + ((velocityHedge == null) ? 0 : velocityHedge.hashCode());
		result = prime * result + ((zeroCostToCustomerIndicator == null) ? 0 : zeroCostToCustomerIndicator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CyberSourceTaxItemRequest other = (CyberSourceTaxItemRequest) obj;
		if (alternateTaxAmount == null) {
			if (other.alternateTaxAmount != null)
				return false;
		} else if (!alternateTaxAmount.equals(other.alternateTaxAmount))
			return false;
		if (alternateTaxID == null) {
			if (other.alternateTaxID != null)
				return false;
		} else if (!alternateTaxID.equals(other.alternateTaxID))
			return false;
		if (alternateTaxRate == null) {
			if (other.alternateTaxRate != null)
				return false;
		} else if (!alternateTaxRate.equals(other.alternateTaxRate))
			return false;
		if (alternateTaxType == null) {
			if (other.alternateTaxType != null)
				return false;
		} else if (!alternateTaxType.equals(other.alternateTaxType))
			return false;
		if (alternateTaxTypeApplied == null) {
			if (other.alternateTaxTypeApplied != null)
				return false;
		} else if (!alternateTaxTypeApplied.equals(other.alternateTaxTypeApplied))
			return false;
		if (buyerRegistration == null) {
			if (other.buyerRegistration != null)
				return false;
		} else if (!buyerRegistration.equals(other.buyerRegistration))
			return false;
		if (cityOverrideAmount == null) {
			if (other.cityOverrideAmount != null)
				return false;
		} else if (!cityOverrideAmount.equals(other.cityOverrideAmount))
			return false;
		if (cityOverrideRate == null) {
			if (other.cityOverrideRate != null)
				return false;
		} else if (!cityOverrideRate.equals(other.cityOverrideRate))
			return false;
		if (commodityCode == null) {
			if (other.commodityCode != null)
				return false;
		} else if (!commodityCode.equals(other.commodityCode))
			return false;
		if (countryOverrideAmount == null) {
			if (other.countryOverrideAmount != null)
				return false;
		} else if (!countryOverrideAmount.equals(other.countryOverrideAmount))
			return false;
		if (countryOverrideRate == null) {
			if (other.countryOverrideRate != null)
				return false;
		} else if (!countryOverrideRate.equals(other.countryOverrideRate))
			return false;
		if (countyOverrideAmount == null) {
			if (other.countyOverrideAmount != null)
				return false;
		} else if (!countyOverrideAmount.equals(other.countyOverrideAmount))
			return false;
		if (countyOverrideRate == null) {
			if (other.countyOverrideRate != null)
				return false;
		} else if (!countyOverrideRate.equals(other.countyOverrideRate))
			return false;
		if (discountAmount == null) {
			if (other.discountAmount != null)
				return false;
		} else if (!discountAmount.equals(other.discountAmount))
			return false;
		if (discountIndicator == null) {
			if (other.discountIndicator != null)
				return false;
		} else if (!discountIndicator.equals(other.discountIndicator))
			return false;
		if (discountRate == null) {
			if (other.discountRate != null)
				return false;
		} else if (!discountRate.equals(other.discountRate))
			return false;
		if (districtOverrideAmount == null) {
			if (other.districtOverrideAmount != null)
				return false;
		} else if (!districtOverrideAmount.equals(other.districtOverrideAmount))
			return false;
		if (districtOverrideRate == null) {
			if (other.districtOverrideRate != null)
				return false;
		} else if (!districtOverrideRate.equals(other.districtOverrideRate))
			return false;
		if (export == null) {
			if (other.export != null)
				return false;
		} else if (!export.equals(other.export))
			return false;
		if (giftCategory == null) {
			if (other.giftCategory != null)
				return false;
		} else if (!giftCategory.equals(other.giftCategory))
			return false;
		if (grossNetIndicator == null) {
			if (other.grossNetIndicator != null)
				return false;
		} else if (!grossNetIndicator.equals(other.grossNetIndicator))
			return false;
		if (hostHedge == null) {
			if (other.hostHedge != null)
				return false;
		} else if (!hostHedge.equals(other.hostHedge))
			return false;
		if (localTax == null) {
			if (other.localTax != null)
				return false;
		} else if (!localTax.equals(other.localTax))
			return false;
		if (middlemanRegistration == null) {
			if (other.middlemanRegistration != null)
				return false;
		} else if (!middlemanRegistration.equals(other.middlemanRegistration))
			return false;
		if (nationalTax == null) {
			if (other.nationalTax != null)
				return false;
		} else if (!nationalTax.equals(other.nationalTax))
			return false;
		if (noExport == null) {
			if (other.noExport != null)
				return false;
		} else if (!noExport.equals(other.noExport))
			return false;
		if (nonsensicalHedge == null) {
			if (other.nonsensicalHedge != null)
				return false;
		} else if (!nonsensicalHedge.equals(other.nonsensicalHedge))
			return false;
		if (obscenitiesHedge == null) {
			if (other.obscenitiesHedge != null)
				return false;
		} else if (!obscenitiesHedge.equals(other.obscenitiesHedge))
			return false;
		if (orderAcceptanceCity == null) {
			if (other.orderAcceptanceCity != null)
				return false;
		} else if (!orderAcceptanceCity.equals(other.orderAcceptanceCity))
			return false;
		if (orderAcceptanceCountry == null) {
			if (other.orderAcceptanceCountry != null)
				return false;
		} else if (!orderAcceptanceCountry.equals(other.orderAcceptanceCountry))
			return false;
		if (orderAcceptanceCounty == null) {
			if (other.orderAcceptanceCounty != null)
				return false;
		} else if (!orderAcceptanceCounty.equals(other.orderAcceptanceCounty))
			return false;
		if (orderAcceptancePostalCode == null) {
			if (other.orderAcceptancePostalCode != null)
				return false;
		} else if (!orderAcceptancePostalCode.equals(other.orderAcceptancePostalCode))
			return false;
		if (orderAcceptanceState == null) {
			if (other.orderAcceptanceState != null)
				return false;
		} else if (!orderAcceptanceState.equals(other.orderAcceptanceState))
			return false;
		if (orderOriginCity == null) {
			if (other.orderOriginCity != null)
				return false;
		} else if (!orderOriginCity.equals(other.orderOriginCity))
			return false;
		if (orderOriginCountry == null) {
			if (other.orderOriginCountry != null)
				return false;
		} else if (!orderOriginCountry.equals(other.orderOriginCountry))
			return false;
		if (orderOriginCounty == null) {
			if (other.orderOriginCounty != null)
				return false;
		} else if (!orderOriginCounty.equals(other.orderOriginCounty))
			return false;
		if (orderOriginPostalCode == null) {
			if (other.orderOriginPostalCode != null)
				return false;
		} else if (!orderOriginPostalCode.equals(other.orderOriginPostalCode))
			return false;
		if (orderOriginState == null) {
			if (other.orderOriginState != null)
				return false;
		} else if (!orderOriginState.equals(other.orderOriginState))
			return false;
		if (phoneHedge == null) {
			if (other.phoneHedge != null)
				return false;
		} else if (!phoneHedge.equals(other.phoneHedge))
			return false;
		if (pointOfTitleTransfer == null) {
			if (other.pointOfTitleTransfer != null)
				return false;
		} else if (!pointOfTitleTransfer.equals(other.pointOfTitleTransfer))
			return false;
		if (productCode == null) {
			if (other.productCode != null)
				return false;
		} else if (!productCode.equals(other.productCode))
			return false;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		if (productRisk == null) {
			if (other.productRisk != null)
				return false;
		} else if (!productRisk.equals(other.productRisk))
			return false;
		if (productSKU == null) {
			if (other.productSKU != null)
				return false;
		} else if (!productSKU.equals(other.productSKU))
			return false;
		if (sellerRegistration == null) {
			if (other.sellerRegistration != null)
				return false;
		} else if (!sellerRegistration.equals(other.sellerRegistration))
			return false;
		if (shipFromCity == null) {
			if (other.shipFromCity != null)
				return false;
		} else if (!shipFromCity.equals(other.shipFromCity))
			return false;
		if (shipFromCountry == null) {
			if (other.shipFromCountry != null)
				return false;
		} else if (!shipFromCountry.equals(other.shipFromCountry))
			return false;
		if (shipFromCounty == null) {
			if (other.shipFromCounty != null)
				return false;
		} else if (!shipFromCounty.equals(other.shipFromCounty))
			return false;
		if (shipFromPostalCode == null) {
			if (other.shipFromPostalCode != null)
				return false;
		} else if (!shipFromPostalCode.equals(other.shipFromPostalCode))
			return false;
		if (shipFromState == null) {
			if (other.shipFromState != null)
				return false;
		} else if (!shipFromState.equals(other.shipFromState))
			return false;
		if (stateOverrideAmount == null) {
			if (other.stateOverrideAmount != null)
				return false;
		} else if (!stateOverrideAmount.equals(other.stateOverrideAmount))
			return false;
		if (stateOverrideRate == null) {
			if (other.stateOverrideRate != null)
				return false;
		} else if (!stateOverrideRate.equals(other.stateOverrideRate))
			return false;
		if (taxAmount == null) {
			if (other.taxAmount != null)
				return false;
		} else if (!taxAmount.equals(other.taxAmount))
			return false;
		if (taxRate == null) {
			if (other.taxRate != null)
				return false;
		} else if (!taxRate.equals(other.taxRate))
			return false;
		if (taxTypeApplied == null) {
			if (other.taxTypeApplied != null)
				return false;
		} else if (!taxTypeApplied.equals(other.taxTypeApplied))
			return false;
		if (timeCategory == null) {
			if (other.timeCategory != null)
				return false;
		} else if (!timeCategory.equals(other.timeCategory))
			return false;
		if (timeHedge == null) {
			if (other.timeHedge != null)
				return false;
		} else if (!timeHedge.equals(other.timeHedge))
			return false;
		if (totalAmount == null) {
			if (other.totalAmount != null)
				return false;
		} else if (!totalAmount.equals(other.totalAmount))
			return false;
		if (unitOfMeasure == null) {
			if (other.unitOfMeasure != null)
				return false;
		} else if (!unitOfMeasure.equals(other.unitOfMeasure))
			return false;
		if (vatRate == null) {
			if (other.vatRate != null)
				return false;
		} else if (!vatRate.equals(other.vatRate))
			return false;
		if (velocityHedge == null) {
			if (other.velocityHedge != null)
				return false;
		} else if (!velocityHedge.equals(other.velocityHedge))
			return false;
		if (zeroCostToCustomerIndicator == null) {
			if (other.zeroCostToCustomerIndicator != null)
				return false;
		} else if (!zeroCostToCustomerIndicator.equals(other.zeroCostToCustomerIndicator))
			return false;
		return true;
	}
    
}
