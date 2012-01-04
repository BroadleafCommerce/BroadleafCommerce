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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import noNamespace.RateV3RequestDocument;
import noNamespace.RateV3RequestType;
import noNamespace.RequestPackageV3Type;
import noNamespace.ShipDateV3Type;

import org.apache.xmlbeans.XmlTokenSource;
import org.broadleafcommerce.common.util.UnitOfMeasureUtil;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;

public class USPSRequestBuilder implements org.broadleafcommerce.vendor.usps.service.message.USPSRequestBuilder {

    public XmlTokenSource buildRequest(USPSShippingPriceRequest request, String username, String password) {
        RateV3RequestDocument doc = RateV3RequestDocument.Factory.newInstance();
        RateV3RequestType v3Request = doc.addNewRateV3Request();
        v3Request.setUSERID(username);
        v3Request.setPASSWORD(password);
        for (USPSContainerItemRequest itemRequest : request.getContainerItems()) {
            RequestPackageV3Type requestPackage = v3Request.addNewPackage();
            if (itemRequest.getContainerShape() != null) {
                requestPackage.setContainer(RequestPackageV3Type.Container.Enum.forString(itemRequest.getContainerShape().getType()));
            }
            if (itemRequest.getFirstClassType() != null) {
                requestPackage.setFirstClassMailType(RequestPackageV3Type.FirstClassMailType.Enum.forString(itemRequest.getFirstClassType().getType()));
            }
            DecimalFormat format = new DecimalFormat("0.#");
            if (itemRequest.getGirth() != null) {
                requestPackage.setGirth(format.format(UnitOfMeasureUtil.findInches(itemRequest.getGirth(), itemRequest.getDimensionUnitOfMeasureType()).doubleValue()));
            }
            if (itemRequest.getHeight() != null) {
                requestPackage.setHeight(format.format(UnitOfMeasureUtil.findInches(itemRequest.getHeight(), itemRequest.getDimensionUnitOfMeasureType()).doubleValue()));
            }
            if (itemRequest.getPackageId() != null) {
                requestPackage.setID(itemRequest.getPackageId());
            }
            if (itemRequest.getDepth() != null) {
                requestPackage.setLength(format.format(UnitOfMeasureUtil.findInches(itemRequest.getDepth(), itemRequest.getDimensionUnitOfMeasureType()).doubleValue()));
            }
            if (itemRequest.isMachineSortable() != null) {
                requestPackage.setMachinable(itemRequest.isMachineSortable());
            }
            if (itemRequest.getWeight() != null) {
                requestPackage.setOunces(format.format(UnitOfMeasureUtil.findRemainingOunces(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()).doubleValue()));
                requestPackage.setPounds(UnitOfMeasureUtil.findWholePounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()));
            }
            if (itemRequest.isReturnLocations() != null) {
                requestPackage.setReturnLocations(itemRequest.isReturnLocations());
            }
            if (itemRequest.getService() != null) {
                requestPackage.setService(RequestPackageV3Type.Service.Enum.forString(itemRequest.getService().getType()));
            }
            if (itemRequest.getShipDate() != null) {
                ShipDateV3Type shipDate = requestPackage.addNewShipDate();
                if (itemRequest.getShipDateOption() != null) {
                    shipDate.setOption(itemRequest.getShipDateOption().getType());
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                shipDate.setStringValue(dateFormat.format(itemRequest.getShipDate()));
            }
            if (itemRequest.getContainerSize() != null) {
                requestPackage.setSize(RequestPackageV3Type.Size.Enum.forString(itemRequest.getContainerSize().getType()));
            }
            if (itemRequest.getWidth() != null) {
                requestPackage.setWidth(format.format(UnitOfMeasureUtil.findInches(itemRequest.getWidth(), itemRequest.getDimensionUnitOfMeasureType()).doubleValue()));
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
