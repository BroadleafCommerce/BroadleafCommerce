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


public class USPSShippingCalculationServiceImpl {

    /*public USPSShippingPriceResponse retrieveShippingRates(USPSShippingPriceRequest request) throws ShipmentPricingException {

    }

    protected void validateRequest(USPSShippingPriceRequest request) {
        for (USPSContainerItemRequest itemRequest : request.getContainerItems()) {
            itemRequest.get
        }
    }

    protected InputStream callUSPSAddressStandardization(Address address) throws IOException {
        URL contentURL = new URL(new StringBuffer(httpProtocol).append("://").append(uspsServerName).append(uspsServiceAPI).toString());
        Map<String, String> content = new HashMap<String, String>();
        content.put("API", "Verify");
        content.put("XML", getAddressXMLString(address));
        return postMessage(content, contentURL, uspsCharSet);
    }

    protected String getAddressXMLString(Address address) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(ADDRESS_VALIDATE_REQUEST_ELEM);
        root.addAttribute(USER_ID_ATTR, uspsUserName);
        root.addAttribute(PASSWORD_ATTR, uspsPassword);

        Element domAddr = root.addElement(ADDRESS_ELEM).addAttribute(ID_ATTR, EMPTY_STRING);
        domAddr.addElement(ADDRESS1_ELEM).setText((address.getAddressLine2() == null) ? EMPTY_STRING : address.getAddressLine2());
        domAddr.addElement(ADDRESS2_ELEM).setText((address.getAddressLine1() == null) ? EMPTY_STRING : address.getAddressLine1());
        domAddr.addElement(CITY_ELEM).setText((address.getCity() == null) ? EMPTY_STRING : address.getCity());
        domAddr.addElement(STATE_ELEM).setText((address.getState() == null) ? EMPTY_STRING : address.getState().getAbbreviation());
        domAddr.addElement(ZIP5_ELEM).setText((address.getPostalCode() == null) ? EMPTY_STRING : address.getPostalCode());
        domAddr.addElement(ZIP4_ELEM).setText((address.getZipFour() == null) ? EMPTY_STRING : address.getZipFour());

        StringWriter strWriter = new StringWriter();
        XMLWriter writer = new XMLWriter(strWriter);

        try {
            writer.write(document);
            if (LOG.isDebugEnabled()) {
                LOG.debug("strWriter.toString(): " + strWriter.toString());
            }
            return strWriter.toString();
        } finally {
            document = null;

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOG.warn("There was an unexpected error closing the Dom4J XMLWriter", e);
                }
            }

            if (strWriter != null) {
                try {
                    strWriter.close();
                } catch (IOException e) {
                    LOG.warn("There was an unexpected error closing a java.io.StringWriter associated with the Dom4J XMLWriter", e);
                }
            }
        }
    }*/
}
