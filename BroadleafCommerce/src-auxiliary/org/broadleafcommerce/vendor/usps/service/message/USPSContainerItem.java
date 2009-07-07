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

import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.usps.service.type.ContainerShapeType;
import org.broadleafcommerce.vendor.usps.service.type.ContainerSizeType;
import org.broadleafcommerce.vendor.usps.service.type.USPSShippingMethodType;

public class USPSContainerItem implements USPSContainerItemRequest, USPSContainerItemResponse {

    protected ContainerSizeType containerSize;
    protected ContainerShapeType containerShape;
    protected boolean isMachineSortable = true;
    protected BigDecimal width;
    protected BigDecimal height;
    protected BigDecimal depth;
    protected BigDecimal girth;
    protected BigDecimal weight;
    protected Date shipDate;
    protected String packageId;
    protected Map<USPSShippingMethodType, Money> rates = new HashMap<USPSShippingMethodType, Money>();
    protected String zipOrigination;
    protected String zipDestination;

    public ContainerSizeType getContainerSize() {
        return containerSize;
    }

    public void setContainerSize(ContainerSizeType containerSize) {
        this.containerSize = containerSize;
    }

    public ContainerShapeType getContainerShape() {
        return containerShape;
    }

    public void setContainerShape(ContainerShapeType containerShape) {
        this.containerShape = containerShape;
    }

    public boolean isMachineSortable() {
        return isMachineSortable;
    }

    public void setMachineSortable(boolean isMachineSortable) {
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

    public Map<USPSShippingMethodType, Money> getRates() {
        return rates;
    }

    public void setRates(Map<USPSShippingMethodType, Money> rates) {
        this.rates = rates;
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

}
