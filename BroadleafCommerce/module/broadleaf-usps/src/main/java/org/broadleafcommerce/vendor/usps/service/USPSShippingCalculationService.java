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

import org.broadleafcommerce.profile.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.profile.vendor.service.type.ServiceStatusType;
import org.broadleafcommerce.vendor.usps.service.message.USPSRequestBuilder;
import org.broadleafcommerce.vendor.usps.service.message.USPSRequestValidator;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponse;

public interface USPSShippingCalculationService {

    public USPSShippingPriceResponse retrieveShippingRates(USPSShippingPriceRequest request) throws ShippingPriceException;

    public ServiceStatusType getServiceStatus();

    public String getUspsCharSet();

    public void setUspsCharSet(String uspsCharSet);

    public String getUspsPassword();

    public void setUspsPassword(String uspsPassword);

    public String getUspsServerName();

    public void setUspsServerName(String uspsServerName);

    public String getUspsServiceAPI();

    public void setUspsServiceAPI(String uspsServiceAPI);

    public String getUspsUserName();

    public void setUspsUserName(String uspsUserName);

    public String getHttpProtocol();

    public void setHttpProtocol(String httpProtocol);

    public Integer getFailureReportingThreshold();

    public void setFailureReportingThreshold(Integer failureReportingThreshold);

    public String getServiceName();

    public String getUspsShippingAPI();

    public void setUspsShippingAPI(String uspsShippingAPI);

    public String getRateRequestElement();

    public void setRateRequestElement(String rateRequestElement);

    public USPSRequestValidator getUspsRequestValidator();

    public void setUspsRequestValidator(USPSRequestValidator uspsRequestValidator);

    public USPSRequestBuilder getUspsRequestBuilder();

    public void setUspsRequestBuilder(USPSRequestBuilder uspsRequestBuilder);
}