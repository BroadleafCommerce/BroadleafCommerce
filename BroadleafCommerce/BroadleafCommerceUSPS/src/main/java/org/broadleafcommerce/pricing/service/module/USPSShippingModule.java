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

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.usps.service.USPSShippingCalculationService;

public class USPSShippingModule implements ShippingModule {

	private static final Log LOG = LogFactory.getLog(USPSShippingModule.class);
	
    private String name = "USPSShippingPriceModule";

    @Resource
    private USPSShippingCalculationService shippingCalculationService;
    
    public FulfillmentGroup calculateShippingForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
    	if (fulfillmentGroup.getFulfillmentGroupItems().size() == 0) {
    		LOG.warn("fulfillment group (" + fulfillmentGroup.getId() + ") does not contain any fulfillment group items. Unable to price USPS shipping");
    		fulfillmentGroup.setShippingPrice(new Money(0D));
            fulfillmentGroup.setSaleShippingPrice(new Money(0D));
            fulfillmentGroup.setRetailShippingPrice(new Money(0D));
    		return fulfillmentGroup;
    	}
    	Address address = fulfillmentGroup.getAddress();
        String state = (address != null && address.getState() != null) ? address.getState().getAbbreviation() : null;
        //TODO finish implementation
//        BigDecimal retailTotal = new BigDecimal(0);
//        String feeType = feeTypeMapping.get(fulfillmentGroup.getMethod());
//        String feeSubType = ((feeSubTypeMapping.get(state) == null) ? feeSubTypeMapping.get("ALL") : feeSubTypeMapping.get(state));
//
//        for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
//            BigDecimal price = (fulfillmentGroupItem.getRetailPrice() != null) ? fulfillmentGroupItem.getRetailPrice().getAmount().multiply(BigDecimal.valueOf(fulfillmentGroupItem.getQuantity())) : null;
//            if (price == null) {
//                price = fulfillmentGroupItem.getOrderItem().getRetailPrice().getAmount().multiply(BigDecimal.valueOf(fulfillmentGroupItem.getQuantity()));
//            }
//            retailTotal = retailTotal.add(price);
//        }
//
//        ShippingRate sr = shippingRateService.readShippingRateByFeeTypesUnityQty(feeType, feeSubType, retailTotal);
//        if (sr == null) {
//            throw new NotImplementedException("Shipping rate " + fulfillmentGroup.getMethod() + " not supported");
//        }
//        BigDecimal shippingPrice = new BigDecimal(0);
//        if (sr.getBandResultPercent().compareTo(0) > 0) {
//            BigDecimal percent = new BigDecimal(sr.getBandResultPercent() / 100);
//            shippingPrice = retailTotal.multiply(percent);
//        } else {
//            shippingPrice = sr.getBandResultQuantity();
//        }
//        fulfillmentGroup.setShippingPrice(new Money(shippingPrice));
//        fulfillmentGroup.setSaleShippingPrice(fulfillmentGroup.getShippingPrice());
//        fulfillmentGroup.setRetailShippingPrice(fulfillmentGroup.getSaleShippingPrice());
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
