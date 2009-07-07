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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.vendor.service.AbstractVendorService;
import org.broadleafcommerce.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.vendor.service.monitor.ServiceStatusDetectable;
import org.broadleafcommerce.vendor.service.type.ServiceStatusType;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponse;
import org.broadleafcommerce.vendor.usps.service.type.ContainerShapeType;
import org.broadleafcommerce.vendor.usps.service.type.ContainerSizeType;
import org.broadleafcommerce.vendor.usps.service.type.USPSShippingPriceErrorCode;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

public class USPSShippingCalculationServiceImpl extends AbstractVendorService implements ServiceStatusDetectable {

    private static final Log LOG = LogFactory.getLog(USPSShippingCalculationServiceImpl.class);

    private static final String USER_ID_ATTR = "USERID";
    private static final String PASSWORD_ATTR = "PASSWORD";
    private static final String RATE_REQUEST_ELEM = "RateV3Request";
    private static final String PACKAGE_ELEM = "Package";
    private static final String ID_ATTR = "ID";
    private static final String SERVICE_ELEM = "Service";
    private static final String SERVICE_TEXT = "ALL";
    private static final String ZIP_ORIGIN_ELEMENT = "ZipOrigination";
    private static final String ZIP_DESTINATION_ELEMENT = "ZipDestination";
    private static final String POUNDS_ELEMENT = "Pounds";

    protected String uspsCharSet;
    protected String uspsPassword;
    protected String uspsServerName;
    protected String uspsServiceAPI;
    protected String uspsUserName;
    protected String httpProtocol;
    protected Integer failureReportingThreshold;
    protected Integer failureCount = 0;
    protected Boolean isUp = true;

    public USPSShippingPriceResponse retrieveShippingRates(USPSShippingPriceRequest request) throws ShippingPriceException {
        validateRequest(request);
        //TODO finish this class
        return null;
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
        for (USPSContainerItemRequest itemRequest : request.getContainerItems()) {
            if (itemRequest.getWeight() == null) {
                throw buildException(USPSShippingPriceErrorCode.WEIGHTNOTSPECIFIED.getType(), USPSShippingPriceErrorCode.WEIGHTNOTSPECIFIED.getMessage());
            }
            if (itemRequest.getWeight().doubleValue() > 70D) {
                throw buildException(USPSShippingPriceErrorCode.OVERWEIGHT.getType(), USPSShippingPriceErrorCode.OVERWEIGHT.getMessage());
            }
            if (itemRequest.getContainerSize().equals(ContainerSizeType.REGULAR) && itemRequest.getContainerShape() == null) {
                throw buildException(USPSShippingPriceErrorCode.SHAPENOTSPECIFIED.getType(), USPSShippingPriceErrorCode.SHAPENOTSPECIFIED.getMessage());
            }
            if (
                    itemRequest.getContainerSize().equals(ContainerSizeType.LARGE) && (
                            itemRequest.getContainerShape().equals(ContainerShapeType.RECTANGULAR) ||
                            itemRequest.getContainerShape().equals(ContainerShapeType.NONRECTANGULAR)
                    ) && (
                            itemRequest.getDepth() == null || itemRequest.getHeight() == null || itemRequest.getWidth() == null
                    )
            ) {
                throw buildException(USPSShippingPriceErrorCode.DIMENSIONSNOTSPECIFIED.getType(), USPSShippingPriceErrorCode.DIMENSIONSNOTSPECIFIED.getMessage());
            }
            if (
                    itemRequest.getContainerSize().equals(ContainerSizeType.LARGE) &&
                    itemRequest.getContainerShape().equals(ContainerShapeType.NONRECTANGULAR) &&
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
        }
    }

    protected ShippingPriceException buildException(String errorCode, String errorText) {
        USPSShippingPriceResponse response = new USPSShippingPriceResponse();
        response.setErrorDetected(true);
        response.setErrorCode(errorCode);
        response.setErrorText(errorText);
        ShippingPriceException e = new ShippingPriceException();
        e.setShippingPriceResponse(response);

        return e;
    }

    protected InputStream callUSPSPricingCalculation(USPSShippingPriceRequest request) throws IOException {
        URL contentURL = new URL(new StringBuffer(httpProtocol).append("://").append(uspsServerName).append(uspsServiceAPI).toString());
        Map<String, String> content = new HashMap<String, String>();
        content.put("API", "RateV3");
        content.put("XML", getShippingPriceXMLString(request));
        return postMessage(content, contentURL, uspsCharSet);
    }

    protected String getShippingPriceXMLString(USPSShippingPriceRequest request) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(RATE_REQUEST_ELEM);
        root.addAttribute(USER_ID_ATTR, uspsUserName);
        root.addAttribute(PASSWORD_ATTR, uspsPassword);

        for (USPSContainerItemRequest itemRequest : request.getContainerItems()) {
            Element dom = root.addElement(PACKAGE_ELEM).addAttribute(ID_ATTR, itemRequest.getPackageId());
            dom.addElement(SERVICE_ELEM).setText(SERVICE_TEXT);
            dom.addElement(ZIP_ORIGIN_ELEMENT).setText(itemRequest.getZipOrigination());
            dom.addElement(ZIP_DESTINATION_ELEMENT).setText(itemRequest.getZipDestination());
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
}
