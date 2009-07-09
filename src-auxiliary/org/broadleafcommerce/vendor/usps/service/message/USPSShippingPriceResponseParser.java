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

import java.util.Currency;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.usps.service.type.USPSShippingMethodType;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class USPSShippingPriceResponseParser extends DefaultHandler {

    private static final Log LOG = LogFactory.getLog(USPSShippingPriceResponseParser.class);

    public static final String PACKAGE_TAG = "Package";
    public static final String POSTAGE_TAG = "Postage";
    public static final String RATE_TAG = "Rate";
    public static final String RESTRICTIONS_TAG = "Restrictions";
    public static final String DESCRIPTION_TAG = "Description";
    public static final String ERROR_TAG = "Error";
    public static final String MAILSERVICE_TAG = "MailService";

    private final USPSShippingPriceRequest request;

    protected USPSShippingPriceResponse shippingPriceResponse = new USPSShippingPriceResponse();
    protected StringBuffer buffer = new StringBuffer();
    protected USPSShippingMethodType shippingMethod = null;

    public USPSShippingPriceResponseParser(USPSShippingPriceRequest request) {
        this.request = request;
    }

    public void characters(char[] ch, int start, int end) {
        buffer.append(ch, start, end);
    }

    public void endElement(String uri, String localName, String qName) {
        if (qName.equals(RATE_TAG)) {
            if (shippingMethod != null) {
                Money rate = new Money(buffer.toString(), Currency.getInstance(Locale.US));
                shippingPriceResponse.getResponses().peek().getRates().put(shippingMethod, rate);
            }
        } else if (qName.equals(RESTRICTIONS_TAG)) {
            shippingPriceResponse.getResponses().peek().setRestrictions(buffer.toString());
        } else if (qName.equals(DESCRIPTION_TAG)) {
            if (shippingPriceResponse.getResponses().empty()) {
                shippingPriceResponse.setErrorText(buffer.toString().trim());
            } else {
                shippingPriceResponse.getResponses().peek().setErrorDetected(true);
                shippingPriceResponse.getResponses().peek().setErrorText(buffer.toString().trim());
            }
        } else if (qName.equals(MAILSERVICE_TAG)) {
            if (shippingMethod == null) {
                shippingMethod = USPSShippingMethodType.getInstanceByDescription(buffer.toString().trim());
                if (shippingMethod == null) {
                    LOG.warn("Shipping method with description ("+buffer.toString().trim()+") not recognized. Not including in results.");
                }
            }
        }
        reset();
    }

    public void reset() {
        buffer = new StringBuffer();
    }

    public USPSShippingPriceResponse getResponse() {
        return shippingPriceResponse;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals(PACKAGE_TAG)) {
            String id = attributes.getValue("ID");
            USPSContainerItem key = new USPSContainerItem();
            key.setPackageId(id);
            USPSContainerItem originalItem = (USPSContainerItem) request.getContainerItems().get(request.getContainerItems().indexOf(key));
            shippingPriceResponse.getResponses().push(originalItem);
        }
        if (qName.equals(POSTAGE_TAG)) {
            String classId = attributes.getValue("CLASSID");
            if (classId != null) {
                shippingMethod = USPSShippingMethodType.getInstance(classId);
                if (shippingMethod == null) {
                    LOG.warn("Shipping method with class id ("+attributes.getValue("CLASSID")+") not recognized. Not including in results.");
                }
            } else {
                shippingMethod = null;
            }
        }
        if (qName.equals(ERROR_TAG)) {
            shippingPriceResponse.setErrorDetected(true);
        }
    }
}
