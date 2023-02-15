/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

/**
 * This class is used in conjunction with the {@link DetermineOfferChangeActivity} to determine if the number
 * of offers changed on the order during the pricing workflow. This is important in determining if an offer
 * expired between the last time the order was priced and when the order was about to be sent through checkout.
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
@Component("blCountTotalOffersActivity")
public class CountTotalOffersActivity extends BaseActivity<ProcessContext<Order>> {

    public static final int ORDER = Integer.MIN_VALUE + 100;

    @Resource(name = "blOfferService")
    protected OfferService offerService;

    public CountTotalOffersActivity() {
        setOrder(ORDER);
    }

    @Override
    public ProcessContext<Order> execute(ProcessContext<Order> context) throws Exception {
        Order order = context.getSeedData();
        Boolean isCheckout = (Boolean) BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties().get(OfferActivity.FINALIZE_CHECKOUT);
        if (isCheckout != null && isCheckout) {
            Set<Long> offers = convertOffersToIds(offerService.getUniqueOffersFromOrder(order));
            BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties().put(OfferActivity.ORIG_OFFERS, offers);
        }
        return context;
    }

    protected Set<Long> convertOffersToIds(Set<Offer> offers) {
        Set<Long> ids = new HashSet<>();
        for (Offer offer : offers) {
            ids.add(offer.getId());
        }
        return ids;
    }
}
