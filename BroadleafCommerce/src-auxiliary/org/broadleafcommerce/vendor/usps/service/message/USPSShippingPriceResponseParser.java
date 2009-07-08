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

    //TODO this element should come in from environment properties
    public static final String RESPONSE_TAG = "RateV2Response";
    public static final String PACKAGE_TAG = "Package";
    public static final String POSTAGE_TAG = "Postage";
    public static final String RATE_TAG = "Rate";
    public static final String RESTRICTIONS_TAG = "Restrictions";
    public static final String DESCRIPTION_TAG = "Description";
    public static final String ERROR_TAG = "Error";

    private USPSShippingPriceResponse shippingPriceResponse = new USPSShippingPriceResponse();
    private StringBuffer buffer = new StringBuffer();
    private USPSShippingMethodType shippingMethod = null;

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
            shippingPriceResponse.setErrorText(buffer.toString().trim());
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
            shippingPriceResponse.getResponses().add(new USPSContainerItem());
            shippingPriceResponse.getResponses().peek().setPackageId(attributes.getValue("ID"));
        }
        if (qName.equals(POSTAGE_TAG)) {
            shippingMethod = USPSShippingMethodType.getInstance(attributes.getValue("CLASSID"));
            if (shippingMethod == null) {
                LOG.warn("Shipping method with class id ("+attributes.getValue("CLASSID")+") not recognized. Not including in results.");
            }
        }
        if (qName.equals(ERROR_TAG)) {
            shippingPriceResponse.setErrorDetected(true);
        }
    }
}
