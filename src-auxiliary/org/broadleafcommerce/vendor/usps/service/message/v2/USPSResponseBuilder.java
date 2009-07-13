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

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import noNamespace.CommitmentV3Type;
import noNamespace.ErrorV3Type;
import noNamespace.LocationV3Type;
import noNamespace.PostageV3Type;
import noNamespace.RateV3ResponseDocument;
import noNamespace.RateV3ResponseType;
import noNamespace.ResponsePackageV3Type;

import org.apache.xmlbeans.XmlException;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.usps.service.message.USPSCommitment;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItem;
import org.broadleafcommerce.vendor.usps.service.message.USPSLocation;
import org.broadleafcommerce.vendor.usps.service.message.USPSPostage;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponse;
import org.broadleafcommerce.vendor.usps.service.type.USPSShippingMethodType;

public class USPSResponseBuilder implements org.broadleafcommerce.vendor.usps.service.message.USPSResponseBuilder {

    public USPSShippingPriceResponse buildResponse(InputStream input, USPSShippingPriceRequest request) {
        USPSShippingPriceResponse shippingPriceResponse;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat dateAndTimeFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
            shippingPriceResponse = new USPSShippingPriceResponse();
            RateV3ResponseDocument doc = RateV3ResponseDocument.Factory.parse(input);
            RateV3ResponseType responseType = doc.getRateV3Response();
            ErrorV3Type[] mainErrors = responseType.getErrorArray();
            if (mainErrors != null && mainErrors.length > 0) {
                shippingPriceResponse.setErrorDetected(true);
                shippingPriceResponse.setErrorCode(mainErrors[0].getSource());
                shippingPriceResponse.setErrorText(mainErrors[0].getDescription());
                return shippingPriceResponse;
            }
            ResponsePackageV3Type[] packages = responseType.getPackageArray();
            for (ResponsePackageV3Type packageItem : packages) {
                String id = packageItem.getID();
                USPSContainerItem key = new USPSContainerItem();
                key.setPackageId(id);
                USPSContainerItem originalItem = (USPSContainerItem) request.getContainerItems().get(request.getContainerItems().indexOf(key));
                shippingPriceResponse.getResponses().push(originalItem);
                if (packageItem.getError() != null) {
                    shippingPriceResponse.setErrorDetected(true);
                    originalItem.setErrorDetected(true);
                    originalItem.setErrorCode(packageItem.getError().getSource());
                    originalItem.setErrorText(packageItem.getError().getDescription());
                }
                PostageV3Type[] postages = packageItem.getPostageArray();
                for (PostageV3Type postage : postages) {
                    int classId = postage.getCLASSID();
                    USPSShippingMethodType shippingMethod = USPSShippingMethodType.getInstance(String.valueOf(classId));
                    USPSPostage uspsPostage = new USPSPostage();
                    if (!postage.xgetCommercialRate().isNil()) {
                        uspsPostage.setCommercialRate(new Money(postage.getCommercialRate()));
                    }
                    uspsPostage.setRate(new Money(postage.getRate()));
                    if (!postage.xgetCommitmentDate().isNil()) {
                        uspsPostage.setCommitmentDate(dateFormat.parse(postage.getCommitmentDate()));
                    }
                    buildLocations(uspsPostage.getLocations(), postage.getLocationArray());
                    for (CommitmentV3Type commitment : postage.getCommitmentArray()) {
                        USPSCommitment uspsCommitment = new USPSCommitment();
                        uspsCommitment.setCommitmentDateAndTime(dateAndTimeFormat.parse(commitment.getCommitmentDate() + " " + commitment.getCommitmentTime()));
                        buildLocations(uspsCommitment.getLocations(), commitment.getLocationArray());
                    }
                    originalItem.getPostage().put(shippingMethod, uspsPostage);
                }
            }
        } catch (XmlException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return shippingPriceResponse;
    }

    public void buildLocations(List<USPSLocation> uspsLocations, LocationV3Type[] locations) {
        for (LocationV3Type location : locations) {
            USPSLocation uspsLocation = new USPSLocation();
            uspsLocation.setCity(location.getCity());
            uspsLocation.setCutoff(location.getCutOff());
            uspsLocation.setFacility(location.getFacility());
            uspsLocation.setState(location.getState());
            uspsLocation.setStreet(location.getStreet());
            uspsLocation.setZip(location.getZip());
            uspsLocations.add(uspsLocation);
        }
    }
}
