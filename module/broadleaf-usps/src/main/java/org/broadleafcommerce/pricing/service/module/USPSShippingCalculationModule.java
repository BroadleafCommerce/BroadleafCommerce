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

package org.broadleafcommerce.pricing.service.module;

import java.util.List;
import java.util.Stack;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.pricing.service.module.ShippingModule;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.vendor.usps.service.USPSShippingCalculationService;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemResponse;
import org.broadleafcommerce.vendor.usps.service.message.USPSPostage;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceRequest;
import org.broadleafcommerce.vendor.usps.service.message.USPSShippingPriceResponse;
import org.broadleafcommerce.vendor.usps.service.type.USPSServiceResponseType;

/**
 * This module will utilize the USPSShippingCalculationService to take your fulfillment group
 * order items and calculate the total shipping rate. Developers must extend this class and
 * implement their own algorithm in the createPackages method for packaging individual items
 * into one or more boxes.
 * 
 * @author jfischer
 *
 */
public abstract class USPSShippingCalculationModule implements ShippingModule {
    
private static final Log LOG = LogFactory.getLog(USPSSingleItemPerPackageShippingCalculationModule.class);
    
    protected String name = "USPSShippingCalculationModule";
    protected String originationPostalCode;
    protected Boolean isDefaultModule = false;
    
    @Resource
    protected USPSShippingCalculationService shippingCalculationService;
    
    public FulfillmentGroup calculateShippingForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) throws ShippingPriceException {
        if (!isValidModuleForService(fulfillmentGroup.getService()) && !isDefaultModule()) {
            LOG.info("fulfillment group (" + fulfillmentGroup.getId() + ") with a service type of (" + fulfillmentGroup.getService() + ") is not valid for this module service type (" + getServiceName() + ")");
            return fulfillmentGroup;
        }
        if (fulfillmentGroup.getFulfillmentGroupItems().size() == 0) {
            LOG.warn("fulfillment group (" + fulfillmentGroup.getId() + ") does not contain any fulfillment group items. Unable to price USPS shipping");
            fulfillmentGroup.setShippingPrice(new Money(0D));
            fulfillmentGroup.setSaleShippingPrice(new Money(0D));
            fulfillmentGroup.setRetailShippingPrice(new Money(0D));
            return fulfillmentGroup;
        }
        List<USPSContainerItemRequest> requestItems = createPackages(fulfillmentGroup);
        USPSShippingPriceRequest request = new USPSShippingPriceRequest();
        request.getContainerItems().addAll(requestItems);
        USPSShippingPriceResponse response = shippingCalculationService.retrieveShippingRates(request);
        Stack<USPSContainerItemResponse> itemResponses = response.getResponses();
        
        USPSServiceResponseType responseType = USPSServiceResponseType.getInstanceByName(fulfillmentGroup.getMethod());
        if (responseType == null) {
            throw new ShippingPriceException("No USPSServiceResponseType found for the shipping method (" + fulfillmentGroup.getMethod() + ") and service type (" + getServiceName() + ")");
        }
        Money shippingPrice = new Money(0D);
        for(USPSContainerItemResponse itemResponse : itemResponses) {
            USPSPostage postage = deducePostage(responseType, requestItems, itemResponse);
            if (postage == null) {
                throw new ShippingPriceException("No postage found in the USPS response for the USPSServiceResponseType (" + responseType.getDescription() + ")");
            }
            shippingPrice = shippingPrice.add(postage.getRate());
        }
        fulfillmentGroup.setShippingPrice(shippingPrice);
        fulfillmentGroup.setSaleShippingPrice(fulfillmentGroup.getShippingPrice());
        fulfillmentGroup.setRetailShippingPrice(fulfillmentGroup.getSaleShippingPrice());
        
        return fulfillmentGroup;
    }
    
    protected USPSPostage deducePostage(USPSServiceResponseType responseType, List<USPSContainerItemRequest> requestItems, USPSContainerItemResponse itemResponse) {
        USPSPostage postage = itemResponse.getPostage().get(responseType);
        if (postage == null) {
            USPSContainerItemRequest itemRequest = findRequestByPackageId(itemResponse.getPackageId(), requestItems);
            if (itemRequest != null) {
                String shape = itemRequest.getContainerShape().getType().replaceAll(" ", "");
                String moreSpecificResponseType = responseType.getName() + shape;
                USPSServiceResponseType nextResponseType = USPSServiceResponseType.getInstanceByName(moreSpecificResponseType);
                if (nextResponseType != null) {
                    postage = itemResponse.getPostage().get(nextResponseType);
                }
            }
            if (postage == null && itemResponse.getPostage().size() == 1) {
                postage = itemResponse.getPostage().values().iterator().next();
            }
        }
        
        return postage;
    }
    
    protected USPSContainerItemRequest findRequestByPackageId(String packageId, List<USPSContainerItemRequest> requestItems) {
        for (USPSContainerItemRequest itemRequest : requestItems) {
            if (itemRequest.getPackageId().equals(packageId)) {
                return itemRequest;
            }
        }
        return null;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    protected abstract List<USPSContainerItemRequest> createPackages(FulfillmentGroup fulfillmentGroup) throws ShippingPriceException;

    public Boolean isValidModuleForService(String serviceName) {
        return getServiceName().equals(serviceName);
    }

    public String getOriginationPostalCode() {
        return originationPostalCode;
    }

    public void setOriginationPostalCode(String originationPostalCode) {
        this.originationPostalCode = originationPostalCode;
    }

    public Boolean isDefaultModule() {
        return isDefaultModule;
    }

    public void setDefaultModule(Boolean isDefaultModule) {
        this.isDefaultModule = isDefaultModule;
    }
    
}
