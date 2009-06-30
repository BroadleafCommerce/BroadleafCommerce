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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.dao.AddressDao;
import org.broadleafcommerce.profile.dao.StateDao;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.vendor.service.AbstractVendorService;
import org.broadleafcommerce.vendor.service.monitor.ServiceStatusDetectable;
import org.broadleafcommerce.vendor.service.type.ServiceStatusType;
import org.broadleafcommerce.vendor.usps.service.connection.AddressStandarizationResponse;
import org.broadleafcommerce.vendor.usps.service.connection.USPSAddressResponseParser;
import org.broadleafcommerce.vendor.usps.service.module.AddressStandardAbbreviations;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@Service("blAddressStandardizationService")
public class AddressStandardizationServiceImpl extends AbstractVendorService implements AddressStandardizationService, ServiceStatusDetectable {

    private static final Log LOG = LogFactory.getLog(AddressStandardizationServiceImpl.class);

    private static final String USER_ID_ATTR = "USERID";
    private static final String PASSWORD_ATTR = "PASSWORD";
    private static final String ID_ATTR = "ID";
    private static final String ADDRESS_VALIDATE_REQUEST_ELEM = "AddressValidateRequest";
    private static final String ADDRESS_ELEM = "Address";
    private static final String ADDRESS1_ELEM = "Address1";
    private static final String ADDRESS2_ELEM = "Address2";
    private static final String CITY_ELEM = "City";
    private static final String STATE_ELEM = "State";
    private static final String ZIP5_ELEM = "Zip5";
    private static final String ZIP4_ELEM = "Zip4";
    private static final String EMPTY_STRING = "";

    protected AddressStandardAbbreviations abbreviations;
    protected String uspsCharSet;
    protected String uspsPassword;
    protected String uspsServerName;
    protected String uspsServiceAPI;
    protected String uspsUserName;
    protected String httpProtocol;
    protected Integer failureReportingThreshold;
    protected Integer failureCount = 0;
    protected Boolean isUp = true;

    @Resource
    protected AddressDao addressDao;
    @Resource
    protected StateDao stateDao;

    @Override
    public AddressStandarizationResponse standardizeAddress(Address address) {
        AddressStandarizationResponse addressStandarizationResponse = new AddressStandarizationResponse();
        InputStream response = null;
        try {
            response = callUSPSAddressStandardization(address);
            ArrayList<AddressStandarizationResponse> AddressResponseList = parseUSPSResponse(response);
            if ((AddressResponseList != null) && !AddressResponseList.isEmpty()) {
                addressStandarizationResponse = AddressResponseList.get(0);
            }
            clearStatus();
            return addressStandarizationResponse;
        } catch (Exception e) {
            LOG.error("Exception", e);
            incrementFailure();
            return getDownResponse("standardizeAddress", new Object[] { address });
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOG.error("IOException while closing the InputStream", e);
                }
            }
        }
    }

    protected void clearStatus() {
        synchronized(failureCount) {
            isUp = true;
            failureCount = 0;
        }
    }

    protected void incrementFailure() {
        synchronized(failureCount) {
            if (failureCount >= failureReportingThreshold) {
                isUp = false;
            } else {
                failureCount++;
            }
        }
    }

    protected void tokenizeAddress(Address addr, boolean isStandardized) {
        String addLine1 = addr.getAddressLine1();
        String addLine2 = addr.getAddressLine2();
        String appendedAddress = "";
        StringBuffer addressToken = new StringBuffer();
        String tokenizedAddress = null;

        if (isStandardized) {
            addressToken.append(addLine1);

            if (addLine2 != null) {
                addressToken.append(" " + addLine2);
            }

            tokenizedAddress = addressToken.toString().replaceAll(" ", EMPTY_STRING);
            tokenizedAddress = tokenizedAddress.toString().toUpperCase();
            tokenizedAddress = tokenizedAddress.replaceAll(",", EMPTY_STRING);
            tokenizedAddress = tokenizedAddress.replaceAll("\\n", EMPTY_STRING);
            tokenizedAddress = tokenizedAddress.replaceAll("\\r", EMPTY_STRING);
            tokenizedAddress = tokenizedAddress.replaceAll("-", EMPTY_STRING);
            tokenizedAddress = tokenizedAddress.replaceAll("#", EMPTY_STRING);
        } else {
            Map<Object, Object> uspsAbbrMap = abbreviations.getAbbreviationsMap();
            addressToken.append(addLine1 + " ");

            if (addLine2 != null) {
                addressToken.append(addLine2 + " ");
            }

            appendedAddress = addressToken.toString().toUpperCase();
            appendedAddress = appendedAddress.replaceAll(",", " ");
            appendedAddress = appendedAddress.replaceAll("\\n", EMPTY_STRING);
            appendedAddress = appendedAddress.replaceAll("\\r", EMPTY_STRING);
            appendedAddress = appendedAddress.replaceAll("#", " ");
            appendedAddress = appendedAddress.replaceAll("-", " ");

            if (uspsAbbrMap != null) {
                Set<Object> stanAbbrMapSet = uspsAbbrMap.keySet();
                Iterator<Object> stanAbbrMapSetIterator = stanAbbrMapSet.iterator();

                while (stanAbbrMapSetIterator.hasNext()) {
                    String addressKey = (String) stanAbbrMapSetIterator.next();

                    if (appendedAddress.indexOf(" " + addressKey + " ") != -1) {
                        String addressValue = (String) uspsAbbrMap.get(addressKey);
                        appendedAddress = appendedAddress.replaceAll(addressKey, addressValue);
                    }
                }

                tokenizedAddress = appendedAddress.replaceAll(" ", EMPTY_STRING);
            }
        }

        addr.setTokenizedAddress(tokenizedAddress);
    }

    protected void standardizeAndTokenizeAddress(Address address) {
        AddressStandarizationResponse standardizationResponse = standardizeAddress(address);

        if (standardizationResponse.isErrorDetected()) {
            address.setStandardized(false);
        } else {
            address.setStandardized(true);
            address = standardizationResponse.getAddress();
        }

        tokenizeAddress(address, !standardizationResponse.isErrorDetected());
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
    }

    protected ArrayList<AddressStandarizationResponse> parseUSPSResponse(InputStream response) throws IOException, SAXException, ParserConfigurationException {
        USPSAddressResponseParser addrContentHelper = new USPSAddressResponseParser(addressDao.create(), stateDao.create());
        SAXParserFactory.newInstance().newSAXParser().parse(response, addrContentHelper);
        return addrContentHelper.getAddressResponseList();
    }

    protected AddressStandarizationResponse getDownResponse(String method, Object[] args) {
        AddressStandarizationResponse addressStandarizationResponse = new AddressStandarizationResponse();
        addressStandarizationResponse.setErrorDetected(true);

        if ("standardizeAndTokenizeAddress".equals(method)) {
            if (args != null && args.length > 0 && args[0] != null) {
                addressStandarizationResponse.setAddress(((Address) args[0]));
            }
        } else if ("standardizeAddress".equals(method)) {
            addressStandarizationResponse.setAddress((Address) args[0]);
        }

        return addressStandarizationResponse;
    }

    @Override
    public ServiceStatusType getServiceStatus() {
        synchronized(failureCount) {
            if (isUp) {
                return ServiceStatusType.UP;
            } else {
                return ServiceStatusType.DOWN;
            }
        }
    }

    public void setAbbreviations(AddressStandardAbbreviations abbreviations) {
        this.abbreviations = abbreviations;
    }

    @Override
    public String getUspsCharSet() {
        return uspsCharSet;
    }

    @Override
    public void setUspsCharSet(String uspsCharSet) {
        this.uspsCharSet = uspsCharSet;
    }

    @Override
    public String getUspsPassword() {
        return uspsPassword;
    }

    @Override
    public void setUspsPassword(String uspsPassword) {
        this.uspsPassword = uspsPassword;
    }

    @Override
    public String getUspsServerName() {
        return uspsServerName;
    }

    @Override
    public void setUspsServerName(String uspsServerName) {
        this.uspsServerName = uspsServerName;
    }

    @Override
    public String getUspsServiceAPI() {
        return uspsServiceAPI;
    }

    @Override
    public void setUspsServiceAPI(String uspsServiceAPI) {
        this.uspsServiceAPI = uspsServiceAPI;
    }

    @Override
    public String getUspsUserName() {
        return uspsUserName;
    }

    @Override
    public void setUspsUserName(String uspsUserName) {
        this.uspsUserName = uspsUserName;
    }

    @Override
    public String getHttpProtocol() {
        return httpProtocol;
    }

    @Override
    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
    }

    @Override
    public Integer getFailureReportingThreshold() {
        return failureReportingThreshold;
    }

    @Override
    public void setFailureReportingThreshold(Integer failureReportingThreshold) {
        this.failureReportingThreshold = failureReportingThreshold;
    }

    @Override
    public String getServiceName() {
        return getClass().getName();
    }
}
