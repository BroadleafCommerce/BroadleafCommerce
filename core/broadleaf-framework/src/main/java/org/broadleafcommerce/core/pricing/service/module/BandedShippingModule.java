/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.pricing.service.module;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOption;
import org.broadleafcommerce.core.pricing.domain.ShippingRate;
import org.broadleafcommerce.core.pricing.service.ShippingRateService;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.BandedFulfillmentPricingProvider;
import org.broadleafcommerce.core.pricing.service.workflow.type.ShippingServiceType;
import org.broadleafcommerce.profile.core.domain.Address;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @deprecated Superceded by functionality given by {@link BandedPriceFulfillmentOption} and {@link BandedFulfillmentPricingProvider}
 * @see {@link BandedPriceFulfillmentOption}, {@link BandedPriceFulfillmentOption}
 */
@Deprecated
public class BandedShippingModule implements ShippingModule {
    
    private static final Log LOG = LogFactory.getLog(BandedShippingModule.class);

    public static final String MODULENAME = "bandedShippingModule";

    protected String name = MODULENAME;
    protected Boolean isDefaultModule = false;

    @Resource(name = "blShippingRateService")
    private ShippingRateService shippingRateService;

    private Map<String, String> feeTypeMapping;
    private Map<String, String> feeSubTypeMapping;

    // this will need to calculate shipping on each fulfilmentGroup in an order
    @Override
    public FulfillmentGroup calculateShippingForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        calculateShipping(fulfillmentGroup);
        return fulfillmentGroup;
    }

    private void calculateShipping(FulfillmentGroup fulfillmentGroup) {
        if (!isValidModuleForService(fulfillmentGroup.getService()) && !isDefaultModule()) {
            LOG.info("fulfillment group (" + fulfillmentGroup.getId() + ") with a service type of (" + fulfillmentGroup.getService() + ") is not valid for this module service type (" + getServiceName() + ")");
            return;
        }
        if (fulfillmentGroup.getFulfillmentGroupItems().size() == 0) {
            LOG.warn("fulfillment group (" + fulfillmentGroup.getId() + ") does not contain any fulfillment group items. Unable to price banded shipping");
            fulfillmentGroup.setShippingPrice(BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, fulfillmentGroup.getOrder().getCurrency()));
            fulfillmentGroup.setSaleShippingPrice(BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, fulfillmentGroup.getOrder().getCurrency()));
            fulfillmentGroup.setRetailShippingPrice(BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, fulfillmentGroup.getOrder().getCurrency()));
            return;
        }
        Address address = fulfillmentGroup.getAddress();
        String state = (address != null && address.getState() != null) ? address.getState().getAbbreviation() : null;
        BigDecimal retailTotal = new BigDecimal(0);
        String feeType = feeTypeMapping.get(fulfillmentGroup.getMethod());
        String feeSubType = ((feeSubTypeMapping.get(state) == null) ? feeSubTypeMapping.get("ALL") : feeSubTypeMapping.get(state));

        for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
            BigDecimal price = (fulfillmentGroupItem.getRetailPrice() != null) ? fulfillmentGroupItem.getRetailPrice().getAmount().multiply(BigDecimal.valueOf(fulfillmentGroupItem.getQuantity())) : null;
            if (price == null) {
                price = fulfillmentGroupItem.getOrderItem().getRetailPrice().getAmount().multiply(BigDecimal.valueOf(fulfillmentGroupItem.getQuantity()));
            }
            retailTotal = retailTotal.add(price);
        }

        ShippingRate sr = shippingRateService.readShippingRateByFeeTypesUnityQty(feeType, feeSubType, retailTotal);
        if (sr == null) {
            throw new NotImplementedException("Shipping rate " + fulfillmentGroup.getMethod() + " not supported");
        }
        BigDecimal shippingPrice = new BigDecimal(0);
        if (sr.getBandResultPercent().compareTo(0) > 0) {
            BigDecimal percent = new BigDecimal(sr.getBandResultPercent() / 100);
            shippingPrice = retailTotal.multiply(percent);
        } else {
            shippingPrice = sr.getBandResultQuantity();
        }
        fulfillmentGroup.setShippingPrice(BroadleafCurrencyUtils.getMoney(shippingPrice, fulfillmentGroup.getOrder().getCurrency()));
        fulfillmentGroup.setSaleShippingPrice(fulfillmentGroup.getShippingPrice());
        fulfillmentGroup.setRetailShippingPrice(fulfillmentGroup.getSaleShippingPrice());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getFeeTypeMapping() {
        return feeTypeMapping;
    }

    public void setFeeTypeMapping(Map<String, String> feeTypeMapping) {
        this.feeTypeMapping = feeTypeMapping;
    }

    public Map<String, String> getFeeSubTypeMapping() {
        return feeSubTypeMapping;
    }

    public void setFeeSubTypeMapping(Map<String, String> feeSubTypeMapping) {
        this.feeSubTypeMapping = feeSubTypeMapping;
    }

    @Override
    public String getServiceName() {
        return ShippingServiceType.BANDED_SHIPPING.getType();
    }

    @Override
    public Boolean isValidModuleForService(String serviceName) {
        return getServiceName().equals(serviceName);
    }

    @Override
    public Boolean isDefaultModule() {
        return isDefaultModule;
    }

    @Override
    public void setDefaultModule(Boolean isDefaultModule) {
        this.isDefaultModule = isDefaultModule;
    }

}
