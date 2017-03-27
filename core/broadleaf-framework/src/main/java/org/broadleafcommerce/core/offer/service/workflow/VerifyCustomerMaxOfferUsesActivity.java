/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.service.workflow;

import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferAuditService;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.offer.service.workflow.extension.VerifyCustomerMaxOfferUsesActivityExtensionHandler;
import org.broadleafcommerce.core.offer.service.workflow.extension.VerifyCustomerMaxOfferUsesActivityExtensionManager;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

/**
 * <p>Checks the offers being used in the order to make sure that the customer
 * has not exceeded the max uses for the {@link Offer}.</p>
 * 
 * This will also verify that max uses for any {@link OfferCode}s that were used to retrieve the {@link Offer}s.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class VerifyCustomerMaxOfferUsesActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    protected static final String BL_PARENT_ORDER = "blParentOrder";

    @Resource(name="blOfferAuditService")
    protected OfferAuditService offerAuditService;
    
    @Resource(name = "blOfferService")
    protected OfferService offerService;

    @Resource(name = "blVerifyCustomerMaxOfferUsesActivityExensionHandler")
    protected VerifyCustomerMaxOfferUsesActivityExtensionManager extensionManager;
    
    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        ExtensionResultHolder<Exception> resultHolder = new ExtensionResultHolder<>();
        resultHolder.setResult(null);
        
        if (extensionManager != null && extensionManager.getProxy() != null) {
            ExtensionResultStatusType result = extensionManager.getProxy().verify(context.getSeedData(), resultHolder);
            
            if (!Objects.equals(ExtensionResultStatusType.NOT_HANDLED, result)) {
                if (resultHolder.getResult() != null) {
                    throw resultHolder.getResult();
                }
            }
        }
        
        return verify(context);
    }
    
    protected ProcessContext<CheckoutSeed> verify(ProcessContext<CheckoutSeed> context) throws OfferMaxUseExceededException {
        Order order = context.getSeedData().getOrder();
        Set<Offer> appliedOffers = offerService.getUniqueOffersFromOrder(order);

        for (Offer offer : appliedOffers) {
            if (offer.isLimitedUsePerCustomer()) {
                Long orderId;
                // In case this is a CSR order, we need to use the parent's ID to look up the OfferAudit data 
                // instead of the current order's ID.
                final Object parentOrder = context.getSeedData().getUserDefinedFields().get(BL_PARENT_ORDER);
                
                if (parentOrder != null) {
                    orderId = ((Order) parentOrder).getId();
                } else {
                    orderId = order.getId();
                }

                Long currentUses = offerAuditService.countUsesByCustomer(orderId, order.getCustomer().getId(), offer.getId());

                if (currentUses >= offer.getMaxUsesPerCustomer()) {
                    throw new OfferMaxUseExceededException("The customer has used this offer more than the maximum allowed number of times.");
                }
            }
        }

        //TODO: allow lenient checking on offer code usage
        for (OfferCode code : order.getAddedOfferCodes()) {
            if (code.isLimitedUse()) {
                Long currentCodeUses = offerAuditService.countOfferCodeUses(order.getId(), code.getId());
                
                if (currentCodeUses >= code.getMaxUses()) {
                    throw new OfferMaxUseExceededException("Offer code " + code.getOfferCode() + " with id " + code.getId()
                                                           + " has been than the maximum allowed number of times.");
                }
            }
        }

        return context;
    }
}
