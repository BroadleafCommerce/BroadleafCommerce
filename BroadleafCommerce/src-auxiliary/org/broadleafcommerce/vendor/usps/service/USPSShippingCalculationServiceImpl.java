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
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.util.DimensionUnitOfMeasureType;
import org.broadleafcommerce.util.UnitOfMeasureUtil;
import org.broadleafcommerce.util.WeightUnitOfMeasureType;
import org.broadleafcommerce.vendor.service.AbstractVendorService;
import org.broadleafcommerce.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.vendor.service.exception.ShippingPriceHostException;
import org.broadleafcommerce.vendor.service.monitor.ServiceStatusDetectable;
import org.broadleafcommerce.vendor.service.type.ServiceStatusType;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponse;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponseParser;
import org.broadleafcommerce.vendor.usps.service.type.USPSContainerShapeType;
import org.broadleafcommerce.vendor.usps.service.type.USPSContainerSizeType;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

public class USPSShippingCalculationServiceImpl extends AbstractVendorService implements ServiceStatusDetectable, USPSShippingCalculationService {

    private static final Log LOG = LogFactory.getLog(USPSShippingCalculationServiceImpl.class);

    private static final String USER_ID_ATTR = "USERID";
    private static final String PASSWORD_ATTR = "PASSWORD";
    private static final String PACKAGE_ELEM = "Package";
    private static final String ID_ATTR = "ID";
    private static final String SERVICE_ELEM = "Service";
    private static final String SERVICE_TEXT = "ALL";
    private static final String ZIP_ORIGIN_ELEMENT = "ZipOrigination";
    private static final String ZIP_DESTINATION_ELEMENT = "ZipDestination";
    private static final String POUNDS_ELEMENT = "Pounds";
    private static final String OUNCES_ELEMENT = "Ounces";
    private static final String SIZE_ELEMENT = "Size";
    private static final String CONTAINER_ELEMENT = "Container";
    private static final String MACHINABLE_ELEMENT = "Machinable";
    private static final String WIDTH_ELEMENT = "Width";
    private static final String LENGTH_ELEMENT = "Length";
    private static final String HEIGHT_ELEMENT = "Height";
    private static final String GIRTH_ELEMENT = "Girth";
    private static final String SHIP_DATE_ELEMENT = "ShipDate";

    protected String uspsCharSet;
    protected String uspsPassword;
    protected String uspsServerName;
    protected String uspsServiceAPI;
    protected String uspsUserName;
    protected String httpProtocol;
    protected Integer failureReportingThreshold;
    protected Integer failureCount = 0;
    protected Boolean isUp = true;
    protected String uspsShippingAPI;
    protected String rateRequestElement;

    public USPSShippingPriceResponse retrieveShippingRates(USPSShippingPriceRequest request) throws ShippingPriceException {
        validateRequest(request);
        USPSShippingPriceResponse shippingPriceResponse = new USPSShippingPriceResponse();
        InputStream response = null;
        try {
            response = callUSPSPricingCalculation(request);
            shippingPriceResponse = parseUSPSResponse(request, response);
        } catch (Exception e) {
            incrementFailure();
            throw new ShippingPriceException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOG.error("IOException while closing the InputStream", e);
                }
            }
        }
        clearStatus();
        if (shippingPriceResponse.isErrorDetected()) {
            ShippingPriceHostException e = new ShippingPriceHostException();
            e.setShippingPriceResponse(shippingPriceResponse);
            throw e;
        }
        return shippingPriceResponse;
    }

    protected USPSShippingPriceResponse parseUSPSResponse(USPSShippingPriceRequest request, InputStream response) throws IOException, SAXException, ParserConfigurationException {
        USPSShippingPriceResponseParser shippingContentHelper = new USPSShippingPriceResponseParser(request);
        SAXParserFactory.newInstance().newSAXParser().parse(response, shippingContentHelper);
        return shippingContentHelper.getResponse();
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

    protected void validateRequest(USPSShippingPriceRequest request) throws ShippingPriceException {
        /*for (USPSContainerItemRequest itemRequest : request.getContainerItems()) {


            if (
                    itemRequest.getContainerSize().equals(USPSContainerSizeType.LARGE) && itemRequest.getContainerShape() != null && (
                            itemRequest.getContainerShape().equals(USPSContainerShapeType.RECTANGULAR) ||
                            itemRequest.getContainerShape().equals(USPSContainerShapeType.NONRECTANGULAR)
                    )
            ) {
                if (itemRequest.getDepth() == null || itemRequest.getHeight() == null || itemRequest.getWidth() == null) {
                    throw buildException(USPSShippingPriceErrorCode.DIMENSIONSNOTSPECIFIED.getType(), USPSShippingPriceErrorCode.DIMENSIONSNOTSPECIFIED.getMessage());
                }
                if (itemRequest.getDimensionUnitOfMeasureType() == null) {
                    throw buildException(USPSShippingPriceErrorCode.UNITTYPENOTSPECIFIED.getType(), USPSShippingPriceErrorCode.UNITTYPENOTSPECIFIED.getMessage());
                }
                if (
                        !itemRequest.getDimensionUnitOfMeasureType().equals(DimensionUnitOfMeasureType.METERS) &&
                        !itemRequest.getDimensionUnitOfMeasureType().equals(DimensionUnitOfMeasureType.FEET) &&
                        !itemRequest.getDimensionUnitOfMeasureType().equals(DimensionUnitOfMeasureType.CENTIMETERS) &&
                        !itemRequest.getDimensionUnitOfMeasureType().equals(DimensionUnitOfMeasureType.INCHES)
                ) {
                    throw buildException(USPSShippingPriceErrorCode.UNITTYPENOTSUPPORTED.getType(), USPSShippingPriceErrorCode.UNITTYPENOTSUPPORTED.getMessage());
                }
            }
            if (
                    itemRequest.getContainerSize().equals(USPSContainerSizeType.LARGE) && itemRequest.getContainerShape() != null &&
                    itemRequest.getContainerShape().equals(USPSContainerShapeType.NONRECTANGULAR) &&
                    itemRequest.getGirth() == null
            ) {
                throw buildException(USPSShippingPriceErrorCode.GIRTHNOTSPECIFIED.getType(), USPSShippingPriceErrorCode.GIRTHNOTSPECIFIED.getMessage());
            }
            if (itemRequest.getPackageId() == null) {
                throw buildException(USPSShippingPriceErrorCode.PACKAGEIDNOTSPECIFIED.getType(), USPSShippingPriceErrorCode.PACKAGEIDNOTSPECIFIED.getMessage());
            }
            if (itemRequest.getZipDestination() == null || itemRequest.getZipOrigination() == null) {
                throw buildException(USPSShippingPriceErrorCode.ZIPNOTSPECIFIED.getType(), USPSShippingPriceErrorCode.ZIPNOTSPECIFIED.getMessage());
            }
            if (itemRequest.getWeightUnitOfMeasureType() == null) {
                throw buildException(USPSShippingPriceErrorCode.UNITTYPENOTSPECIFIED.getType(), USPSShippingPriceErrorCode.UNITTYPENOTSPECIFIED.getMessage());
            }
            if (!itemRequest.getWeightUnitOfMeasureType().equals(WeightUnitOfMeasureType.KILOGRAMS) && !itemRequest.getWeightUnitOfMeasureType().equals(WeightUnitOfMeasureType.POUNDS)) {
                throw buildException(USPSShippingPriceErrorCode.UNITTYPENOTSUPPORTED.getType(), USPSShippingPriceErrorCode.UNITTYPENOTSUPPORTED.getMessage());
            }
            Calendar maxAdvance = Calendar.getInstance();
            maxAdvance.add(Calendar.DATE, 3);
            if (itemRequest.getShipDate() != null && itemRequest.getShipDate().getTime() > maxAdvance.getTime().getTime()) {
                throw buildException(USPSShippingPriceErrorCode.SHIPDATETOOFAR.getType(), USPSShippingPriceErrorCode.SHIPDATETOOFAR.getMessage());
            }
        }*/
    }



    protected InputStream callUSPSPricingCalculation(USPSShippingPriceRequest request) throws IOException {
        URL contentURL = new URL(new StringBuffer(httpProtocol).append("://").append(uspsServerName).append(uspsServiceAPI).toString());
        Map<String, String> content = new HashMap<String, String>();
        content.put("API", uspsShippingAPI);
        content.put("XML", getShippingPriceXMLString(request));
        return postMessage(content, contentURL, uspsCharSet);
    }



    protected String getShippingPriceXMLString(USPSShippingPriceRequest request) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(rateRequestElement);
        root.addAttribute(USER_ID_ATTR, uspsUserName);
        root.addAttribute(PASSWORD_ATTR, uspsPassword);

        for (USPSContainerItemRequest itemRequest : request.getContainerItems()) {
            Element dom = root.addElement(PACKAGE_ELEM).addAttribute(ID_ATTR, itemRequest.getPackageId());
            dom.addElement(SERVICE_ELEM).setText(SERVICE_TEXT);
            dom.addElement(ZIP_ORIGIN_ELEMENT).setText(itemRequest.getZipOrigination());
            dom.addElement(ZIP_DESTINATION_ELEMENT).setText(itemRequest.getZipDestination());
            dom.addElement(POUNDS_ELEMENT).setText(findPounds(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()));
            dom.addElement(OUNCES_ELEMENT).setText(findOunces(itemRequest.getWeight(), itemRequest.getWeightUnitOfMeasureType()));
            dom.addElement(SIZE_ELEMENT).setText(itemRequest.getContainerSize().getType());
            if (itemRequest.getContainerSize().equals(USPSContainerSizeType.LARGE) && itemRequest.getContainerShape() != null) {
                dom.addElement(CONTAINER_ELEMENT).setText(itemRequest.getContainerShape().getType());
            }
            dom.addElement(MACHINABLE_ELEMENT).setText(Boolean.toString(itemRequest.isMachineSortable()));
            if (itemRequest.getContainerSize().equals(USPSContainerSizeType.LARGE)){
                if (
                        itemRequest.getContainerShape() != null &&
                        (
                                itemRequest.getContainerShape().equals(USPSContainerShapeType.NONRECTANGULAR) ||
                                itemRequest.getContainerShape().equals(USPSContainerShapeType.RECTANGULAR)
                        )
                ) {
                    dom.addElement(WIDTH_ELEMENT).setText(findInches(itemRequest.getWidth(), itemRequest.getDimensionUnitOfMeasureType()));
                    dom.addElement(HEIGHT_ELEMENT).setText(findInches(itemRequest.getHeight(), itemRequest.getDimensionUnitOfMeasureType()));
                    dom.addElement(LENGTH_ELEMENT).setText(findInches(itemRequest.getDepth(), itemRequest.getDimensionUnitOfMeasureType()));
                }
                if (itemRequest.getContainerShape() != null && itemRequest.getContainerShape().equals(USPSContainerShapeType.NONRECTANGULAR)) {
                    dom.addElement(GIRTH_ELEMENT).setText(findInches(itemRequest.getGirth(), itemRequest.getDimensionUnitOfMeasureType()));
                }
            }
            if (itemRequest.getShipDate() != null) {
                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                dom.addElement(SHIP_DATE_ELEMENT).setText(format.format(itemRequest.getShipDate()));
            }
        }

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

    public String findPounds(BigDecimal weight, WeightUnitOfMeasureType type) {
        int pounds = UnitOfMeasureUtil.findWholePounds(weight, type);
        return String.valueOf(pounds);
    }

    public String findOunces(BigDecimal weight, WeightUnitOfMeasureType type) {
        BigDecimal ounces = UnitOfMeasureUtil.findRemainingOunces(weight, type);
        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(ounces.doubleValue());
    }

    public String findInches(BigDecimal dimension, DimensionUnitOfMeasureType type) {
        dimension = UnitOfMeasureUtil.findInches(dimension, type);
        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(dimension.doubleValue());
    }

    public ServiceStatusType getServiceStatus() {
        synchronized(failureCount) {
            if (isUp) {
                return ServiceStatusType.UP;
            } else {
                return ServiceStatusType.DOWN;
            }
        }
    }

    public String getUspsCharSet() {
        return uspsCharSet;
    }

    public void setUspsCharSet(String uspsCharSet) {
        this.uspsCharSet = uspsCharSet;
    }

    public String getUspsPassword() {
        return uspsPassword;
    }

    public void setUspsPassword(String uspsPassword) {
        this.uspsPassword = uspsPassword;
    }

    public String getUspsServerName() {
        return uspsServerName;
    }

    public void setUspsServerName(String uspsServerName) {
        this.uspsServerName = uspsServerName;
    }

    public String getUspsServiceAPI() {
        return uspsServiceAPI;
    }

    public void setUspsServiceAPI(String uspsServiceAPI) {
        this.uspsServiceAPI = uspsServiceAPI;
    }

    public String getUspsUserName() {
        return uspsUserName;
    }

    public void setUspsUserName(String uspsUserName) {
        this.uspsUserName = uspsUserName;
    }

    public String getHttpProtocol() {
        return httpProtocol;
    }

    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
    }

    public Integer getFailureReportingThreshold() {
        return failureReportingThreshold;
    }

    public void setFailureReportingThreshold(Integer failureReportingThreshold) {
        this.failureReportingThreshold = failureReportingThreshold;
    }

    public String getServiceName() {
        return getClass().getName();
    }

    public String getUspsShippingAPI() {
        return uspsShippingAPI;
    }

    public void setUspsShippingAPI(String uspsShippingAPI) {
        this.uspsShippingAPI = uspsShippingAPI;
    }

    public String getRateRequestElement() {
        return rateRequestElement;
    }

    public void setRateRequestElement(String rateRequestElement) {
        this.rateRequestElement = rateRequestElement;
    }
}
