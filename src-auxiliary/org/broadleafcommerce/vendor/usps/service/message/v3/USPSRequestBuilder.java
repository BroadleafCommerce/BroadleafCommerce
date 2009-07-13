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

import org.broadleafcommerce.util.UnitOfMeasureUtil;
import org.broadleafcommerce.vendor.usps.service.message.AbstractUSPSRequestBuilder;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;

public class USPSRequestBuilder extends AbstractUSPSRequestBuilder {

    @Override
    public String buildRequest(USPSShippingPriceRequest request) {
        RateV3RequestDocument doc = RateV3RequestDocument.Factory.newInstance();
        RateV3RequestType v3Request = doc.addNewRateV3Request();
        for (USPSContainerItemRequest itemRequest : request.getContainerItems()) {
            RequestPackageType requestPackage = v3Request.addNewPackage();
            requestPackage.setContainer(RequestPackageType.Container.Enum.forString(itemRequest.getContainerShape().getType()));
            requestPackage.setFirstClassMailType(RequestPackageType.FirstClassMailType.Enum.forString(itemRequest.getFirstClassType().getType()));
            requestPackage.setGirth(itemRequest.getGirth().floatValue());
            requestPackage.setHeight(itemRequest.getHeight().floatValue());
            requestPackage.setID(itemRequest.getPackageId());
            requestPackage.setLength(itemRequest.getDepth().floatValue());
            requestPackage.setMachinable(itemRequest.isMachineSortable());
            requestPackage.setOunces(UnitOfMeasureUtil.findRemainingOunces(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).floatValue());
            requestPackage.setPounds(UnitOfMeasureUtil.findWholePounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()));
            requestPackage.setReturnLocations(itemRequest.isReturnLocations());
        }
        return null;
    }

}
