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

import org.broadleafcommerce.profile.vendor.service.exception.ShippingPriceException;

public interface USPSVersionedRequestValidator {

    public void validateWeight(USPSContainerItemRequest itemRequest) throws ShippingPriceException;

    public void validateService(USPSContainerItemRequest itemRequest) throws ShippingPriceException;

    public void validateContainer(USPSContainerItemRequest itemRequest) throws ShippingPriceException;

    public void validateSize(USPSContainerItemRequest itemRequest) throws ShippingPriceException;

    public void validateMachinable(USPSContainerItemRequest itemRequest) throws ShippingPriceException;

    public void validateDimensions(USPSContainerItemRequest itemRequest) throws ShippingPriceException;

    public void validateGirth(USPSContainerItemRequest itemRequest) throws ShippingPriceException;

    public void validateShipDate(USPSContainerItemRequest itemRequest) throws ShippingPriceException;

    public void validateOther(USPSContainerItemRequest itemRequest) throws ShippingPriceException;

}
