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
package org.broadleafcommerce.vendor.usps.service;

import java.math.BigDecimal;

import org.broadleafcommerce.util.UnitOfMeasureUtil;
import org.broadleafcommerce.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.type.USPSServiceV2Type;
import org.broadleafcommerce.vendor.usps.service.type.USPSShippingPriceErrorCode;

public class USPSV2RequestValidator {

    public void validateService(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        //do nothing
    }

    public void validateWeight(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        USPSServiceV2Type service = (USPSServiceV2Type) itemRequest.getService();
        if (service.equals(USPSServiceV2Type.FIRSTCLASS)) {
            //13 ounces for first class
            if (UnitOfMeasureUtil.findPounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).doubleValue() > UnitOfMeasureUtil.convertOuncesToPounds(BigDecimal.valueOf(13)).doubleValue()) {
                throw USPSRequestValidator.buildException(USPSShippingPriceErrorCode.OVERWEIGHT.getType(), USPSShippingPriceErrorCode.OVERWEIGHT.getMessage());
            }
        }
        if (service.equals(USPSServiceV2Type.BPM)) {
            //15 pounds for bpm
            if (UnitOfMeasureUtil.findPounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).doubleValue() > 15D) {
                throw USPSRequestValidator.buildException(USPSShippingPriceErrorCode.OVERWEIGHT.getType(), USPSShippingPriceErrorCode.OVERWEIGHT.getMessage());
            }
        }
        if (UnitOfMeasureUtil.findPounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).doubleValue() > 70D) {
            throw USPSRequestValidator.buildException(USPSShippingPriceErrorCode.OVERWEIGHT.getType(), USPSShippingPriceErrorCode.OVERWEIGHT.getMessage());
        }
    }

}
