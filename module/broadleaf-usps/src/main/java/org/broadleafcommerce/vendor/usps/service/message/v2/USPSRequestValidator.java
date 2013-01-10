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

package org.broadleafcommerce.vendor.usps.service.message.v2;

import java.math.BigDecimal;

import org.broadleafcommerce.profile.util.UnitOfMeasureUtil;
import org.broadleafcommerce.profile.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSVersionedRequestValidator;
import org.broadleafcommerce.vendor.usps.service.type.USPSServiceType;
import org.broadleafcommerce.vendor.usps.service.type.USPSShippingPriceErrorCode;

public class USPSRequestValidator implements USPSVersionedRequestValidator {

    public void validateService(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        // validate the type is compatible with the v2 schema
        if (!itemRequest.getService().equals(USPSServiceType.ALL) && !itemRequest.getService().equals(USPSServiceType.FIRSTCLASS) && !itemRequest.getService().equals(USPSServiceType.PRIORITY) && !itemRequest.getService().equals(USPSServiceType.EXPRESS) && !itemRequest.getService().equals(USPSServiceType.BPM) && !itemRequest.getService().equals(USPSServiceType.PARCEL) && !itemRequest.getService().equals(USPSServiceType.MEDIA) && !itemRequest.getService().equals(USPSServiceType.LIBRARY)) {
            throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.SERVICENOTSUPPORTED.getType(), USPSShippingPriceErrorCode.SERVICENOTSUPPORTED.getMessage());
        }
    }

    public void validateWeight(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        USPSServiceType service = itemRequest.getService();
        if (service.equals(USPSServiceType.FIRSTCLASS)) {
            // 13 ounces for first class
            if (UnitOfMeasureUtil.findPounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).doubleValue() > UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(13)).doubleValue()) {
                throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.OVERWEIGHT.getType(), USPSShippingPriceErrorCode.OVERWEIGHT.getMessage());
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
        if (itemRequest.getContainerSize() == null) {
            throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.SIZENOTSPECIFIED.getType(), USPSShippingPriceErrorCode.SIZENOTSPECIFIED.getMessage());
        }
    }

    public void validateContainer(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        if ((itemRequest.getService().equals(USPSServiceType.EXPRESS) || itemRequest.getService().equals(USPSServiceType.PRIORITY)) && itemRequest.getContainerShape() == null) {
            throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.SHAPENOTSPECIFIED.getType(), USPSShippingPriceErrorCode.SHAPENOTSPECIFIED.getMessage());
        }
    }

    public void validateMachinable(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        if ((itemRequest.getService().equals(USPSServiceType.ALL) || itemRequest.getService().equals(USPSServiceType.ONLINE) || itemRequest.getService().equals(USPSServiceType.PARCEL)) && itemRequest.isMachineSortable() == null) {
            throw org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator.buildException(USPSShippingPriceErrorCode.MACHINABLESPECIFIED.getType(), USPSShippingPriceErrorCode.MACHINABLESPECIFIED.getMessage());
        }
    }

    public void validateDimensions(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        // do nothing
    }

    public void validateGirth(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        // do nothing
    }

    public void validateShipDate(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        // do nothing
    }

    public void validateOther(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        // do nothing
    }

}
