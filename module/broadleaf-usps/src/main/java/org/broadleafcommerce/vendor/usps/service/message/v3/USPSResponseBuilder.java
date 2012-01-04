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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import noNamespace.CommitmentV3Type;
import noNamespace.ErrorDocument;
import noNamespace.ErrorV2Type;
import noNamespace.LocationV3Type;
import noNamespace.PostageV3Type;
import noNamespace.RateV3ResponseDocument;
import noNamespace.RateV3ResponseType;
import noNamespace.ResponsePackageV3Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.vendor.usps.service.message.USPSCommitment;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItem;
import org.broadleafcommerce.vendor.usps.service.message.USPSLocation;
import org.broadleafcommerce.vendor.usps.service.message.USPSPostage;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponse;
import org.broadleafcommerce.vendor.usps.service.type.USPSServiceResponseType;

public class USPSResponseBuilder implements org.broadleafcommerce.vendor.usps.service.message.USPSResponseBuilder {

    private static final Log LOG = LogFactory.getLog(USPSResponseBuilder.class);

    public USPSShippingPriceResponse buildResponse(InputStream hostInput, USPSShippingPriceRequest request) {
        USPSShippingPriceResponse shippingPriceResponse = new USPSShippingPriceResponse();
        RateV3ResponseDocument doc;
        String xml = null;
        try {
            xml = generateXml(hostInput);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Host Response: " + xml);
            }
            doc = RateV3ResponseDocument.Factory.parse(xml);
        } catch (XmlException e) {
            if (xml != null) {
                try {
                    ErrorDocument error = ErrorDocument.Factory.parse(xml);
                    ErrorV2Type errorType = error.getError();
                    shippingPriceResponse.setErrorDetected(true);
                    shippingPriceResponse.setErrorCode(String.valueOf(errorType.getNumber()));
                    shippingPriceResponse.setErrorText(errorType.getDescription());
                    return shippingPriceResponse;
                } catch (XmlException e1) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat dateAndTimeFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
            RateV3ResponseType responseType = doc.getRateV3Response();
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
                    originalItem.setErrorCode(String.valueOf(packageItem.getError().getNumber()));
                    originalItem.setErrorText(packageItem.getError().getDescription());
                }
                originalItem.setZone(packageItem.getZone());
                if (packageItem.xgetRestrictions()!=null) {
                    originalItem.setRestrictions(packageItem.getRestrictions());
                }
                PostageV3Type[] postages = packageItem.getPostageArray();
                for (PostageV3Type postage : postages) {
                    int classId = postage.getCLASSID();
                    USPSServiceResponseType shippingMethod = USPSServiceResponseType.getInstance(String.valueOf(classId));
                    USPSPostage uspsPostage = new USPSPostage();
                    if (postage.xgetCommercialRate()!=null) {
                        uspsPostage.setCommercialRate(new Money(postage.getCommercialRate()));
                    }
                    uspsPostage.setRate(new Money(postage.getRate()));
                    if (postage.xgetCommitmentDate()!=null) {
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
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return shippingPriceResponse;
    }

    private String generateXml(InputStream hostInput) throws IOException {
        StringBuffer sb = new StringBuffer();
        boolean eof = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(hostInput));
            while (!eof) {
                String temp = reader.readLine();
                if (temp == null) {
                    eof = true;
                } else {
                    sb.append(temp);
                    sb.append("\n");
                }
            }
        } finally {
            if (reader != null) {
                try{ reader.close(); } catch (Throwable e) {}
            }
        }
        return sb.toString();
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
