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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.order.domain.BundleOrderItem;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.type.USPSServiceMethod;
import org.broadleafcommerce.pricing.service.workflow.type.ShippingServiceType;
import org.broadleafcommerce.vendor.service.exception.ShippingPriceException;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItem;
import org.broadleafcommerce.vendor.usps.service.message.USPSContainerItemRequest;
import org.broadleafcommerce.vendor.usps.service.type.USPSContainerSizeType;
import org.broadleafcommerce.vendor.usps.service.type.USPSServiceType;

/**
 * This implementation of the USPSShippingCalculationModule will package each fulfillment group
 * order item in its own box for the purpose of USPS shipping price calculation. This will generally
 * not be the most useful approach, as more than one item will generally be able to be included in a 
 * single shipping box. Developers may wish to extend USPSShippingCalculationModule with their own
 * custom implementation, or utilize the BandedShippingModule for estimated shipping.
 * 
 * @author jfischer
 *
 */
public class USPSSingleItemPerPackageShippingCalculationModule extends USPSShippingCalculationModule {

	private static final Log LOG = LogFactory.getLog(USPSSingleItemPerPackageShippingCalculationModule.class);
    
    @Override
	protected List<USPSContainerItemRequest> createPackages(FulfillmentGroup fulfillmentGroup) throws ShippingPriceException {
    	List<USPSContainerItemRequest> itemRequests = new ArrayList<USPSContainerItemRequest>();
    	for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {
    		List<DiscreteOrderItem> discreteItems = new ArrayList<DiscreteOrderItem>();
    		OrderItem orderItem = fgItem.getOrderItem();
    		if(BundleOrderItem.class.isAssignableFrom(orderItem.getClass())) {
    			discreteItems.addAll(((BundleOrderItem) orderItem).getDiscreteOrderItems());
    		} else if (GiftWrapOrderItem.class.isAssignableFrom(orderItem.getClass())) {
    			List<OrderItem> wrappedItems = ((GiftWrapOrderItem) orderItem).getWrappedItems();
    			if (!fulfillmentGroup.getOrder().getOrderItems().containsAll(wrappedItems)){
    				throw new ShippingPriceException("To price shipping correctly, the items contained in the GiftWrapOrderItem must also individually appear in the order, not just in the wrappedItems collection of GiftWrapOrderItem.");
    			}
    			continue;
    		} else if (DiscreteOrderItem.class.isAssignableFrom(orderItem.getClass())) {
    			discreteItems.add((DiscreteOrderItem) orderItem);
    		}
    		
    		int counter = 0;
    		for (DiscreteOrderItem discreteItem : discreteItems) {
    			itemRequests.add(createRequest(fulfillmentGroup, discreteItem, counter));
    			counter++;
    		}
    	}
    	
    	return itemRequests;
    }

	public String getServiceName() {
		return ShippingServiceType.USPS.getType();
	}

	protected USPSContainerItemRequest createRequest(FulfillmentGroup fulfillmentGroup, DiscreteOrderItem discreteItem, int counter) throws ShippingPriceException {
    	USPSServiceMethod uspsMethod = USPSServiceMethod.getInstance(fulfillmentGroup.getMethod());
    	if (uspsMethod == null) {
    		throw new ShippingPriceException("Unable to find a USPSShippingMethod for the method found on the fulfillment group: (" + fulfillmentGroup.getMethod() + ")");
    	}
    	USPSServiceType serviceType = USPSServiceType.getInstanceByServiceMethod(uspsMethod);
    	if (serviceType == null) {
    		throw new ShippingPriceException("Unable to establish a USPSServiceType for the USPSServiceMethod: (" + uspsMethod.getType() + ")");
    	}
    	USPSContainerItemRequest itemRequest = new USPSContainerItem();
        itemRequest.setService(serviceType);
        Product product = discreteItem.getProduct();
        itemRequest.setContainerSize((USPSContainerSizeType) product.getSize());
        itemRequest.setPackageId(String.valueOf(counter));
        itemRequest.setWeight(product.getWeight().getWeight());
        itemRequest.setWeightUnitOfMeasureType(product.getWeight().getWeightUnitOfMeasure());
        itemRequest.setZipDestination(fulfillmentGroup.getAddress().getPostalCode());
        itemRequest.setZipOrigination(getOriginationPostalCode());
        itemRequest.setMachineSortable(product.isMachineSortable());
        
        return itemRequest;
    }
}
