/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.pricing.service.workflow;

import org.apache.commons.collections4.CollectionUtils;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.OfferValueModifierExtensionManager;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.annotation.Resource;

@Component("blOfferActivity")
public class OfferActivity extends BaseActivity<ProcessContext<Order>> {

    public static final int ORDER = 1000;
    public static final String FINALIZE_CHECKOUT = "FINALIZE_CHECKOUT";
    public static final String OFFERS_EXPIRED = "OFFERS_EXPIRED";
    public static final String ORIG_OFFERS = "ORIG_OFFERS";
    
    @Resource(name="blOfferService")
    protected OfferService offerService;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blOfferValueModifierExtensionManager")
    protected OfferValueModifierExtensionManager offerModifierExtensionManager;
    
    public OfferActivity() {
        setOrder(ORDER);
    }

    @Override
    public ProcessContext<Order> execute(ProcessContext<Order> context) throws Exception {
        Order order = context.getSeedData();
        List<OfferCode> offerCodes = getNewOfferCodesFromCustomer(order);

        if (offerCodes != null && !offerCodes.isEmpty()) {
            order = orderService.addOfferCodes(order, offerCodes, false);
        }

        List<Offer> offers = offerService.buildOfferListForOrder(order);

        if (CollectionUtils.isNotEmpty(offers) && offerModifierExtensionManager != null) {
            offerModifierExtensionManager.getProxy().modifyOfferValues(offers, order);
        }

        order = offerService.applyAndSaveOffersToOrder(offers, order);

        context.setSeedData(order);

        return context;
    }

    protected List<OfferCode> getNewOfferCodesFromCustomer(Order order) {
        List<OfferCode> offerCodesFromCustomer = offerService.buildOfferCodeListForCustomer(order);
        List<OfferCode> offerCodesFromOrder = order.getAddedOfferCodes();

        offerCodesFromCustomer.removeAll(offerCodesFromOrder);

        return offerCodesFromCustomer;
    }

}
