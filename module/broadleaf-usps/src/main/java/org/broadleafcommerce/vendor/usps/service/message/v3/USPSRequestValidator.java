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

package org.broadleafcommerce.vendor.usps.service.message.v3;

import java.math.BigDecimal;
import java.util.Calendar;

import org.broadleafcommerce.common.util.DimensionUnitOfMeasureType;
import org.broadleafcommerce.common.util.UnitOfMeasureUtil;
import org.broadleafcommerce.common.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSVersionedRequestValidator;
import org.broadleafcommerce.vendor.usps.service.type.USPSContainerShapeType;
import org.broadleafcommerce.vendor.usps.service.type.USPSContainerSizeType;
import org.broadleafcommerce.vendor.usps.service.type.USPSFirstClassType;
import org.broadleafcommerce.vendor.usps.service.type.USPSServiceType;
import org.broadleafcommerce.vendor.usps.service.type.USPSShippingPriceErrorCode;

public class USPSRequestValidator implements USPSVersionedRequestValidator {

    public void validateService(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        USPSServiceType service = itemRequest.getService();
        if (service.equals(USPSServiceType.FIRSTCLASS) && itemRequest.getFirstClassType() == null) {
            throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.FIRSTCLASSNOTSPECIFIED.getType(), USPSShippingPriceErrorCode.FIRSTCLASSNOTSPECIFIED.getMessage());
        }
    }

    public void validateWeight(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        USPSServiceType service = itemRequest.getService();
        if (service.equals(USPSServiceType.FIRSTCLASS)) {
            // 3.5 ounces for letter
            if (itemRequest.getFirstClassType().equals(USPSFirstClassType.LETTER)) {
                if (UnitOfMeasureUtil.findPounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).doubleValue() > UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(3.5)).doubleValue()) {
                    throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.OVERWEIGHT.getType(), USPSShippingPriceErrorCode.OVERWEIGHT.getMessage());
                }
            } else {
                // 13 ounces for other first class
                if (UnitOfMeasureUtil.findPounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).doubleValue() > UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(13)).doubleValue()) {
                    throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.OVERWEIGHT.getType(), USPSShippingPriceErrorCode.OVERWEIGHT.getMessage());
                }
            }
        }
        if (service.equals(USPSServiceType.BPM)) {
            // 15 pounds for bpm
            if (UnitOfMeasureUtil.findPounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).doubleValue() > 15D) {
                throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.OVERWEIGHT.getType(), USPSShippingPriceErrorCode.OVERWEIGHT.getMessage());
            }
        }
        if (UnitOfMeasureUtil.findPounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).doubleValue() > 70D) {
            throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.OVERWEIGHT.getType(), USPSShippingPriceErrorCode.OVERWEIGHT.getMessage());
        }
    }

    public void validateSize(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        if ((itemRequest.getService().equals(USPSServiceType.ALL) || itemRequest.getService().equals(USPSServiceType.ONLINE) || itemRequest.getService().equals(USPSServiceType.PARCEL) || itemRequest.getService().equals(USPSServiceType.BPM) || itemRequest.getService().equals(USPSServiceType.MEDIA) || itemRequest.getService().equals(USPSServiceType.LIBRARY)) && itemRequest.getContainerSize() == null) {
            throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.SIZENOTSPECIFIED.getType(), USPSShippingPriceErrorCode.SIZENOTSPECIFIED.getMessage());
        }
    }

    public void validateContainer(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        // do nothing
    }

    public void validateMachinable(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        if ((itemRequest.getService().equals(USPSServiceType.ALL) || itemRequest.getService().equals(USPSServiceType.ONLINE) || itemRequest.getService().equals(USPSServiceType.PARCEL) || (itemRequest.getService().equals(USPSServiceType.FIRSTCLASS) && (itemRequest.getFirstClassType().equals(USPSFirstClassType.LETTER) || itemRequest.getFirstClassType().equals(USPSFirstClassType.FLAT)))) && itemRequest.isMachineSortable() == null) {
            throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.MACHINABLESPECIFIED.getType(), USPSShippingPriceErrorCode.MACHINABLESPECIFIED.getMessage());
        }
    }

    public void validateDimensions(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        if ((itemRequest.getService().equals(USPSServiceType.ALL) || itemRequest.getService().equals(USPSServiceType.ONLINE) || itemRequest.getService().equals(USPSServiceType.PRIORITY) || itemRequest.getService().equals(USPSServiceType.PRIORITYCOMMERCIAL)) && itemRequest.getContainerSize().equals(USPSContainerSizeType.LARGE) && itemRequest.getContainerShape() != null && (itemRequest.getContainerShape().equals(USPSContainerShapeType.RECTANGULAR) || itemRequest.getContainerShape().equals(USPSContainerShapeType.NONRECTANGULAR))) {
            if (itemRequest.getDepth() == null || itemRequest.getHeight() == null || itemRequest.getWidth() == null) {
                throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.DIMENSIONSNOTSPECIFIED.getType(), USPSShippingPriceErrorCode.DIMENSIONSNOTSPECIFIED.getMessage());
            }
            if (itemRequest.getDimensionUnitOfMeasureType() == null) {
                throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.UNITTYPENOTSPECIFIED.getType(), USPSShippingPriceErrorCode.UNITTYPENOTSPECIFIED.getMessage());
            }
            if (!itemRequest.getDimensionUnitOfMeasureType().equals(DimensionUnitOfMeasureType.METERS) && !itemRequest.getDimensionUnitOfMeasureType().equals(DimensionUnitOfMeasureType.FEET) && !itemRequest.getDimensionUnitOfMeasureType().equals(DimensionUnitOfMeasureType.CENTIMETERS) && !itemRequest.getDimensionUnitOfMeasureType().equals(DimensionUnitOfMeasureType.INCHES)) {
                throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.UNITTYPENOTSUPPORTED.getType(), USPSShippingPriceErrorCode.UNITTYPENOTSUPPORTED.getMessage());
            }
        }
    }

    public void validateGirth(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        if ((itemRequest.getService().equals(USPSServiceType.ALL) || itemRequest.getService().equals(USPSServiceType.ONLINE) || itemRequest.getService().equals(USPSServiceType.PRIORITY) || itemRequest.getService().equals(USPSServiceType.PRIORITYCOMMERCIAL)) && itemRequest.getContainerSize().equals(USPSContainerSizeType.LARGE) && itemRequest.getContainerShape() != null && itemRequest.getContainerShape().equals(USPSContainerShapeType.NONRECTANGULAR) && itemRequest.getGirth() == null) {
            throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.GIRTHNOTSPECIFIED.getType(), USPSShippingPriceErrorCode.GIRTHNOTSPECIFIED.getMessage());
        }
    }

    public void validateShipDate(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        Calendar maxAdvance = Calendar.getInstance();
        maxAdvance.add(Calendar.DATE, 3);
        if (itemRequest.getShipDate() != null && itemRequest.getShipDate().getTime() > maxAdvance.getTime().getTime()) {
            throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.SHIPDATETOOFAR.getType(), USPSShippingPriceErrorCode.SHIPDATETOOFAR.getMessage());
        }
    }

    public void validateOther(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        // do nothing
    }
}
