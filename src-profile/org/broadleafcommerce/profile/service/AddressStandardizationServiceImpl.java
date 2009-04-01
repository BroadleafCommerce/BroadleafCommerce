package org.broadleafcommerce.profile.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.service.addressValidation.AddressStandardAbbreviations;
import org.broadleafcommerce.profile.service.addressValidation.AddressStandarizationResponse;
import org.broadleafcommerce.profile.service.addressValidation.ServiceDownResponse;
import org.broadleafcommerce.profile.service.addressValidation.USPSAddressResponseParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@Service("addressStandardizationService")
public class AddressStandardizationServiceImpl implements AddressStandardizationService, ServiceDownResponse {
    protected final Log logger = LogFactory.getLog(getClass());
    private static final String HTTP_PROTOCOL = "http://";
    private static final String POST_METHOD = "POST";
    private static final String API_PARAM = "API=Verify&";
    private static final String XML_PARAM = "XML=";
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
    private AddressStandardAbbreviations abbreviations;
    // TODO: Should access these from property file.
    private String uspsCharSet = "UTF-8";
    private String uspsPassword = "338MC69CR570";
    private String uspsServerName = "testing.shippingapis.com";
    private String uspsServiceAPI = "/ShippingAPITest.dll";
    private String uspsUserName = "482CREDE3966";

    public void setAbbreviations(AddressStandardAbbreviations abbreviations) {
        this.abbreviations = abbreviations;
    }

    public void setUspsCharSet(String uspsCharSet) {
        this.uspsCharSet = uspsCharSet;
    }

    public void setUspsPassword(String uspsPassword) {
        this.uspsPassword = uspsPassword;
    }

    public void setUspsServerName(String uspsServerName) {
        this.uspsServerName = uspsServerName;
    }

    public void setUspsServiceAPI(String uspsServiceAPI) {
        this.uspsServiceAPI = uspsServiceAPI;
    }

    public void setUspsUserName(String uspsUserName) {
        this.uspsUserName = uspsUserName;
    }

    public AddressStandarizationResponse standardizeAddress(Address address) {
        AddressStandarizationResponse addressStandarizationResponse = new AddressStandarizationResponse();

        InputStream response = null;

        try {
            response = callUSPSAddressStandardization(address);

            ArrayList<AddressStandarizationResponse> AddressResponseList = parseUSPSResponse(response);

            if ((AddressResponseList != null) && !AddressResponseList.isEmpty()) {
                addressStandarizationResponse = AddressResponseList.get(0);
            }
            return addressStandarizationResponse;
        } catch (IOException e) {
            logger.error("IOException", e);
            return (AddressStandarizationResponse) getDownResponse("standardizeAddress", new Object[] { address });
        } catch (SAXException e) {
            logger.error("SAXException", e);
            return (AddressStandarizationResponse) getDownResponse("standardizeAddress", new Object[] { address });
        } catch (ParserConfigurationException e) {
            logger.error("ParserConfigurationException", e);
            return (AddressStandarizationResponse) getDownResponse("standardizeAddress", new Object[] { address });
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("IOException while closing the InputStream", e);
                }
            }
        }
    }

    public void tokenizeAddress(Address addr, boolean isStandardized) {
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

    private InputStream callUSPSAddressStandardization(Address address) throws IOException {

        URL contentURL = new URL(new StringBuffer(HTTP_PROTOCOL).append(uspsServerName).append(uspsServiceAPI).toString());

        HttpURLConnection connection = (HttpURLConnection) contentURL.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod(POST_METHOD);

        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
        osw.write(API_PARAM);
        osw.write(XML_PARAM);
        osw.write(getAddressXMLString(address));
        osw.flush();

        try {
            osw.close();
        } catch (IOException e) {
            // We'll try to avoid stopping processing and just log the error if the OutputStream doesn't close
            logger.error("Problem closing the OuputStream to the USPS Service", e);
        }

        return new BufferedInputStream(connection.getInputStream());
    }

    private String getAddressXMLString(Address address) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(ADDRESS_VALIDATE_REQUEST_ELEM);
        root.addAttribute(USER_ID_ATTR, uspsUserName);
        root.addAttribute(PASSWORD_ATTR, uspsPassword);

        Element domAddr = root.addElement(ADDRESS_ELEM).addAttribute(ID_ATTR, EMPTY_STRING);
        domAddr.addElement(ADDRESS1_ELEM).setText((address.getAddressLine2() == null) ? EMPTY_STRING : address.getAddressLine2());
        domAddr.addElement(ADDRESS2_ELEM).setText((address.getAddressLine1() == null) ? EMPTY_STRING : address.getAddressLine1());
        domAddr.addElement(CITY_ELEM).setText((address.getCity() == null) ? EMPTY_STRING : address.getCity());
        domAddr.addElement(STATE_ELEM).setText((address.getStateProvRegion() == null) ? EMPTY_STRING : address.getStateProvRegion().getShortName());
        domAddr.addElement(ZIP5_ELEM).setText((address.getPostalCode() == null) ? EMPTY_STRING : address.getPostalCode());
        domAddr.addElement(ZIP4_ELEM).setText((address.getZipFour() == null) ? EMPTY_STRING : address.getZipFour());

        StringWriter strWriter = new StringWriter();
        XMLWriter writer = new XMLWriter(strWriter);

        try {
            writer.write(document);
            logger.debug("strWriter.toString(): " + strWriter.toString());
            return URLEncoder.encode(strWriter.toString(), uspsCharSet);
        } finally {
            document = null;

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.warn("There was an unexpected error closing the Dom4J XMLWriter", e);
                }
            }

            if (strWriter != null) {
                try {
                    strWriter.close();
                } catch (IOException e) {
                    logger.warn("There was an unexpected error closing a java.io.StringWriter associated with the Dom4J XMLWriter", e);
                }
            }
        }
    }

    private ArrayList<AddressStandarizationResponse> parseUSPSResponse(InputStream response) throws IOException, SAXException, ParserConfigurationException {
        USPSAddressResponseParser addrContentHelper = new USPSAddressResponseParser();
        // FileOutputStream fos = new FileOutputStream("/temp/" + System.currentTimeMillis() + ".xml");
        // int nextChar;
        // while ((nextChar = response.read()) != -1)
        // fos.write(Character.toUpperCase((char) nextChar));
        // fos.write('\n');
        // fos.flush();

        SAXParserFactory.newInstance().newSAXParser().parse(response, addrContentHelper);
        return addrContentHelper.getAddressResponseList();
    }

    public void standardizeAndTokenizeAddress(Address address) {
        AddressStandarizationResponse standardizationResponse = standardizeAddress(address);

        if (standardizationResponse.isErrorDetected()) {
            address.setStandardized(false);
        } else {
            address.setStandardized(true);
            address = standardizationResponse.getAddress();
        }

        tokenizeAddress(address, !standardizationResponse.isErrorDetected());
    }

    public Object getDownResponse(String method, Object[] args) {
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
}
