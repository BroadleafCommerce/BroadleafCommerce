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

package org.broadleafcommerce.vendor.usps.service.message;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.common.util.DimensionUnitOfMeasureType;
import org.broadleafcommerce.common.util.WeightUnitOfMeasureType;
import org.broadleafcommerce.vendor.usps.service.type.USPSContainerShapeType;
import org.broadleafcommerce.vendor.usps.service.type.USPSContainerSizeType;
import org.broadleafcommerce.vendor.usps.service.type.USPSFirstClassType;
import org.broadleafcommerce.vendor.usps.service.type.USPSServiceResponseType;
import org.broadleafcommerce.vendor.usps.service.type.USPSServiceType;
import org.broadleafcommerce.vendor.usps.service.type.USPSShipDateOptionType;

public class USPSContainerItem implements USPSContainerItemRequest, USPSContainerItemResponse {

    //input
    protected USPSServiceType service;
    protected USPSContainerSizeType containerSize;
    protected USPSContainerShapeType containerShape;
    protected Boolean isMachineSortable;
    protected BigDecimal width;
    protected BigDecimal height;
    protected BigDecimal depth;
    protected BigDecimal girth;
    protected BigDecimal weight;
    protected Date shipDate;
    protected USPSShipDateOptionType shipDateOption;
    protected String packageId;
    protected String zipOrigination;
    protected String zipDestination;
    protected WeightUnitOfMeasureType weightUnitOfMeasureType;
    protected DimensionUnitOfMeasureType dimensionUnitOfMeasureType;
    protected USPSFirstClassType firstClassType;
    protected Boolean isReturnLocations;

    //output
    protected Map<USPSServiceResponseType, USPSPostage> postage = new HashMap<USPSServiceResponseType, USPSPostage>();
    protected String restrictions;
    protected boolean isErrorDetected = false;
    protected String errorCode;
    protected String errorText;
    protected String zone;

    public USPSContainerSizeType getContainerSize() {
        return containerSize;
    }

    public void setContainerSize(USPSContainerSizeType containerSize) {
        this.containerSize = containerSize;
    }

    public USPSContainerShapeType getContainerShape() {
        return containerShape;
    }

    public void setContainerShape(USPSContainerShapeType containerShape) {
        this.containerShape = containerShape;
    }

    public Boolean isMachineSortable() {
        return isMachineSortable;
    }

    public void setMachineSortable(Boolean isMachineSortable) {
        this.isMachineSortable = isMachineSortable;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getDepth() {
        return depth;
    }

    public void setDepth(BigDecimal depth) {
        this.depth = depth;
    }

    public BigDecimal getGirth() {
        return girth;
    }

    public void setGirth(BigDecimal girth) {
        this.girth = girth;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId.trim();
    }

    public Map<USPSServiceResponseType, USPSPostage> getPostage() {
        return postage;
    }

    public void setPostage(Map<USPSServiceResponseType, USPSPostage> postage) {
        this.postage = postage;
    }

    public String getZipOrigination() {
        return zipOrigination;
    }

    public void setZipOrigination(String zipOrigination) {
        this.zipOrigination = zipOrigination.trim();
    }

    public String getZipDestination() {
        return zipDestination;
    }

    public void setZipDestination(String zipDestination) {
        this.zipDestination = zipDestination.trim();
    }

    public WeightUnitOfMeasureType getWeightUnitOfMeasureType() {
        return weightUnitOfMeasureType;
    }

    public void setWeightUnitOfMeasureType(WeightUnitOfMeasureType weightUnitOfMeasureType) {
        this.weightUnitOfMeasureType = weightUnitOfMeasureType;
    }

    public DimensionUnitOfMeasureType getDimensionUnitOfMeasureType() {
        return dimensionUnitOfMeasureType;
    }

    public void setDimensionUnitOfMeasureType(DimensionUnitOfMeasureType dimensionUnitOfMeasureType) {
        this.dimensionUnitOfMeasureType = dimensionUnitOfMeasureType;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public boolean isErrorDetected() {
        return isErrorDetected;
    }

    public void setErrorDetected(boolean isErrorDetected) {
        this.isErrorDetected = isErrorDetected;
    }

    public USPSServiceType getService() {
        return service;
    }

    public void setService(USPSServiceType service) {
        this.service = service;
    }

    public USPSFirstClassType getFirstClassType() {
        return firstClassType;
    }

    public void setFirstClassType(USPSFirstClassType firstClassType) {
        this.firstClassType = firstClassType;
    }

    public Boolean isReturnLocations() {
        return isReturnLocations;
    }

    public void setReturnLocations(Boolean isReturnLocations) {
        this.isReturnLocations = isReturnLocations;
    }

    public USPSShipDateOptionType getShipDateOption() {
        return shipDateOption;
    }

    public void setShipDateOption(USPSShipDateOptionType shipDateOption) {
        this.shipDateOption = shipDateOption;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        USPSContainerItem other = (USPSContainerItem) obj;
        if (packageId == null) {
            if (other.packageId != null)
                return false;
        } else if (!packageId.equals(other.packageId))
            return false;
        return true;
    }

}
