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
package org.broadleafcommerce.profile.service.addressValidation;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class USPSAddressResponseParser extends DefaultHandler {
    protected final Log logger = LogFactory.getLog(getClass());
    public static final String ADDRESS_VALIDATE_REQUEST_TAG = "AddressValidateRequest";
    public static final String ADDRESS_TAG = "Address";
    public static final String ADDRESS1_TAG = "Address1";
    public static final String ADDRESS2_TAG = "Address2";
    public static final String CITY_TAG = "City";
    public static final String STATE_TAG = "State";
    public static final String ZIP5_TAG = "Zip5";
    public static final String ZIP4_TAG = "Zip4";
    public static final String RETURN_TEXT_TAG = "ReturnText";
    public static final String ERROR_TAG = "Error";
    public static final String NUMBER_TAG = "Number";
    public static final String SOURCE_TAG = "Source";
    public static final String DESCRIPTION_TAG = "Description";
    public static final String HELP_FILE_TAG = "HelpFile";
    public static final String HELP_CONTEXT_TAG = "HelpContext";
    private Address address;
    private AddressStandarizationResponse addressStandarizationResponse;
    private ArrayList<AddressStandarizationResponse> addressResponseList = new ArrayList<AddressStandarizationResponse>();
    private StringBuffer buffer = new StringBuffer();

    public void characters(char[] ch, int start, int end) {
        buffer.append(ch, start, end);
    }

    public void endElement(String uri, String localName, String qName) {
        if (qName.equals(ADDRESS_TAG)) {
            addressResponseList.add(addressStandarizationResponse);
        } else if (qName.equals(ADDRESS1_TAG)) {
            //Address 1 and 2 are reversed on purpose
            address.setAddressLine2(buffer.toString().trim());
            buffer = new StringBuffer();
        } else if (qName.equals(ADDRESS2_TAG)) {
            //Address 1 and 2 are reversed on purpose
            address.setAddressLine1(buffer.toString().trim());
            buffer = new StringBuffer();
        } else if (qName.equals(CITY_TAG)) {
            address.setCity(buffer.toString().trim());
            buffer = new StringBuffer();
        } else if (qName.equals(STATE_TAG)) {
            State state = new StateImpl();
            state.setAbbreviation(buffer.toString().trim());
            address.setState(state);
            buffer = new StringBuffer();
        } else if (qName.equals(ZIP5_TAG)) {
            address.setPostalCode(buffer.toString().trim());
            buffer = new StringBuffer();
        } else if (qName.equals(ZIP4_TAG)) {
            address.setZipFour(buffer.toString().trim());
            buffer = new StringBuffer();
        } else if (qName.equals(RETURN_TEXT_TAG)) {
            addressStandarizationResponse.setErrorDetected(true);
            addressStandarizationResponse.setReturnText(buffer.toString().trim());
            buffer = new StringBuffer();
        } else if (qName.equals(NUMBER_TAG)) {
            buffer = new StringBuffer();
        } else if (qName.equals(SOURCE_TAG)) {
            buffer = new StringBuffer();
        } else if (qName.equals(DESCRIPTION_TAG)) {
            buffer = new StringBuffer();
        } else if (qName.equals(HELP_FILE_TAG)) {
            buffer = new StringBuffer();
        } else if (qName.equals(HELP_CONTEXT_TAG)) {
            buffer = new StringBuffer();
        }
    }

    public ArrayList<AddressStandarizationResponse> getAddressResponseList() {
        return addressResponseList;
    }

    public void reset() {
        buffer = new StringBuffer();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals(ADDRESS_TAG)) {
            addressStandarizationResponse = new AddressStandarizationResponse();
            address = new AddressImpl();
            addressStandarizationResponse.setAddress(address);
        }

        if (qName.equals(ERROR_TAG)) {
            addressStandarizationResponse.setErrorDetected(true);
        }
    }
}
