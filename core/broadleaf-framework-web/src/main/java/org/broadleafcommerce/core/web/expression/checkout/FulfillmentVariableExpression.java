/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.expression.checkout;

import org.broadleafcommerce.common.vendor.service.exception.FulfillmentPriceException;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.FulfillmentOptionService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.pricing.service.FulfillmentPricingService;
import org.broadleafcommerce.core.pricing.service.fulfillment.provider.FulfillmentEstimationResponse;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.core.web.order.service.CartStateService;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Component("blFulfillmentVariableExpression")
@ConditionalOnTemplating
public class FulfillmentVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blCartStateService")
    protected CartStateService cartStateService;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blFulfillmentOptionService")
    protected FulfillmentOptionService fulfillmentOptionService;

    @Resource(name = "blFulfillmentPricingService")
    protected FulfillmentPricingService fulfillmentPricingService;

    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    @Override
    public String getName() {
        return "fulfillment";
    }

    public int getNumShippableFulfillmentGroups() {
        Order cart = CartState.getCart();

        return fulfillmentGroupService.calculateNumShippableFulfillmentGroups(cart);
    }

    public List<FulfillmentOption> getFulfillmentOptions() {
        return fulfillmentOptionService.readAllFulfillmentOptions();
    }

    public List<OrderMultishipOption> getMultiShipOptions() {
        Order cart = CartState.getCart();

        if (!isNullOrder(cart)) {
            return orderMultishipOptionService.getOrGenerateOrderMultishipOptions(cart);
        }
        return new ArrayList<>();
    }

    public FulfillmentEstimationResponse getFulfillmentEstimateResponse() {
        Order cart = CartState.getCart();

        if (!isNullOrder(cart) && cart.getFulfillmentGroups().size() > 0 && cartStateService.cartHasPopulatedShippingAddress()) {
            try {
                List<FulfillmentOption> fulfillmentOptions = fulfillmentOptionService.readAllFulfillmentOptions();
                FulfillmentGroup firstShippableFulfillmentGroup = fulfillmentGroupService.getFirstShippableFulfillmentGroup(cart);
                return fulfillmentPricingService.estimateCostForFulfillmentGroup(firstShippableFulfillmentGroup, new HashSet<>(fulfillmentOptions));
            } catch (FulfillmentPriceException e) {
                // do nothing
            }
        }
        return null;
    }

    protected boolean isNullOrder(Order cart) {
        return cart == null || (cart instanceof NullOrderImpl);
    }


}
