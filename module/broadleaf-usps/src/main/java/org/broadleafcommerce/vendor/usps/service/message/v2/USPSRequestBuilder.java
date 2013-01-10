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

import java.text.DecimalFormat;

import noNamespace.RateV2RequestDocument;
import noNamespace.RateV2RequestType;
import noNamespace.RequestPackageV2Type;

import org.apache.xmlbeans.XmlTokenSource;
import org.broadleafcommerce.common.util.UnitOfMeasureUtil;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;

public class USPSRequestBuilder implements org.broadleafcommerce.vendor.usps.service.message.USPSRequestBuilder {

    public XmlTokenSource buildRequest(USPSShippingPriceRequest request, String username, String password) {
        RateV2RequestDocument doc = RateV2RequestDocument.Factory.newInstance();
        RateV2RequestType v2Request = doc.addNewRateV2Request();
        v2Request.setUSERID(username);
        v2Request.setPASSWORD(password);
        for (USPSContainerItemRequest itemRequest : request.getContainerItems()) {
            RequestPackageV2Type requestPackage = v2Request.addNewPackage();
            if (itemRequest.getContainerShape() != null) {
                // for some reason, in version 2, the container name must start
                // with a capital letter with lower-case letters following for
                // each word
                String containerShape = itemRequest.getContainerShape().getType();
                String[] tokens = containerShape.split(" ");
                StringBuffer sb = new StringBuffer();
                for (String token : tokens) {
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(token.substring(0, 1));
                    sb.append(token.substring(1, token.length()).toLowerCase());
                }
                requestPackage.setContainer(RequestPackageV2Type.Container.Enum.forString(sb.toString()));
            }
            if (itemRequest.getPackageId() != null) {
                requestPackage.setID(itemRequest.getPackageId());
            }
            if (itemRequest.isMachineSortable() != null) {
                requestPackage.setMachinable(itemRequest.isMachineSortable());
            }
            if (itemRequest.getWeight() != null) {
                DecimalFormat format = new DecimalFormat("0.#");
                requestPackage.setOunces(format.format(UnitOfMeasureUtil.findRemainingOunces(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).doubleValue()));
                requestPackage.setPounds(UnitOfMeasureUtil.findWholePounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()));
            }
            if (itemRequest.getService() != null) {
                requestPackage.setService(RequestPackageV2Type.Service.Enum.forString(itemRequest.getService().getType()));
            }
            if (itemRequest.getContainerSize() != null) {
                requestPackage.setSize(RequestPackageV2Type.Size.Enum.forString(itemRequest.getContainerSize().getType()));
            }
            if (itemRequest.getZipDestination() != null) {
                requestPackage.setZipDestination(Integer.valueOf(itemRequest.getZipDestination()));
            }
            if (itemRequest.getZipOrigination() != null) {
                requestPackage.setZipOrigination(Integer.valueOf(itemRequest.getZipOrigination()));
            }
        }
        return doc;
    }

}
