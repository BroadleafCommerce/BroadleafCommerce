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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlTokenSource;
import org.broadleafcommerce.profile.vendor.service.AbstractVendorService;
import org.broadleafcommerce.profile.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.profile.vendor.service.exception.ShippingPriceHostException;
import org.broadleafcommerce.profile.vendor.service.monitor.ServiceStatusDetectable;
import org.broadleafcommerce.profile.vendor.service.type.ServiceStatusType;
import org.broadleafcommerce.vendor.usps.service.message.USPSRequestBuilder;
import org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator;
import org.broadleafcommerce.vendor.usps.service.message.USPSResponseBuilder;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponse;

public class USPSShippingCalculationServiceImpl extends AbstractVendorService implements ServiceStatusDetectable, USPSShippingCalculationService {

    private static final Log LOG = LogFactory.getLog(USPSShippingCalculationServiceImpl.class);

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
    protected USPSRequestValidator uspsRequestValidator;
    protected USPSRequestBuilder uspsRequestBuilder;
    protected USPSResponseBuilder uspsResponseBuilder;

    public USPSShippingPriceResponse retrieveShippingRates(USPSShippingPriceRequest request) throws ShippingPriceException {
        uspsRequestValidator.validateRequest(request);
        USPSShippingPriceResponse shippingPriceResponse = new USPSShippingPriceResponse();
        InputStream response = null;
        try {
            response = callUSPSPricingCalculation(request);
            shippingPriceResponse = uspsResponseBuilder.buildResponse(response, request);
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

    protected InputStream callUSPSPricingCalculation(USPSShippingPriceRequest request) throws IOException {
        URL contentURL = new URL(new StringBuffer(httpProtocol).append("://").append(uspsServerName).append(uspsServiceAPI).toString());
        Map<String, String> content = new HashMap<String, String>();
        content.put("API", uspsShippingAPI);
        XmlTokenSource doc = uspsRequestBuilder.buildRequest(request, uspsUserName, uspsPassword);
        String text = doc.xmlText();
        if (LOG.isDebugEnabled()) {
            LOG.debug("xml request source: " + text);
        }
        content.put("XML", text);
        return postMessage(content, contentURL, uspsCharSet);
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

    public USPSRequestValidator getUspsRequestValidator() {
        return uspsRequestValidator;
    }

    public void setUspsRequestValidator(USPSRequestValidator uspsRequestValidator) {
        this.uspsRequestValidator = uspsRequestValidator;
    }

    public USPSRequestBuilder getUspsRequestBuilder() {
        return uspsRequestBuilder;
    }

    public void setUspsRequestBuilder(USPSRequestBuilder uspsRequestBuilder) {
        this.uspsRequestBuilder = uspsRequestBuilder;
    }

    public USPSResponseBuilder getUspsResponseBuilder() {
        return uspsResponseBuilder;
    }

    public void setUspsResponseBuilder(USPSResponseBuilder uspsResponseBuilder) {
        this.uspsResponseBuilder = uspsResponseBuilder;
    }
}
