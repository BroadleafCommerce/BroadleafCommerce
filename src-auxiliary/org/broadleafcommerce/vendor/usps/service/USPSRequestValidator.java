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

import org.broadleafcommerce.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponse;
import org.broadleafcommerce.vendor.usps.service.type.USPSShippingPriceErrorCode;

public class USPSRequestValidator {

    private final VersionedRequestValidator versionedValidator;

    public USPSRequestValidator(VersionedRequestValidator versionedValidator) {
        this.versionedValidator = versionedValidator;
    }

    public void validateRequest(USPSShippingPriceRequest request) throws ShippingPriceException {
        validatePackageQuantity(request);
        for (USPSContainerItemRequest itemRequest : request.getContainerItems()) {
            versionedValidator.validateService(itemRequest);
        }
    }

    protected void validateWeight(USPSContainerItemRequest itemRequest) throws ShippingPriceException {
        if (itemRequest.getWeight() == null) {
            throw buildException(USPSShippingPriceErrorCode.WEIGHTNOTSPECIFIED.getType(), USPSShippingPriceErrorCode.WEIGHTNOTSPECIFIED.getMessage());
        }
        versionedValidator.validateWeight(itemRequest);

    }

    protected void validatePackageQuantity(USPSShippingPriceRequest request) throws ShippingPriceException {
        if (request.getContainerItems().size() > 25) {
            throw buildException(USPSShippingPriceErrorCode.TOOMANYCONTAINERITEMS.getType(), USPSShippingPriceErrorCode.TOOMANYCONTAINERITEMS.getMessage());
        }
    }

    public static ShippingPriceException buildException(String errorCode, String errorText) {
        USPSShippingPriceResponse response = new USPSShippingPriceResponse();
        response.setErrorDetected(true);
        response.setErrorCode(errorCode);
        response.setErrorText(errorText);
        ShippingPriceException e = new ShippingPriceException();
        e.setShippingPriceResponse(response);

        return e;
    }
}
