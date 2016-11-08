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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionManagerOperation;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Andre Azzolini (apazzolini), bpolster, Jeff Fischer
 */
@Service("blOfferServiceExtensionManager")
public class OfferServiceExtensionManager extends ExtensionManager<OfferServiceExtensionHandler> implements OfferServiceExtensionHandler {

    public static final String STOP_PROCESSING = "stopProcessing";

    public static final ExtensionManagerOperation applyAdditionalFilters = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferServiceExtensionHandler) handler).applyAdditionalFilters((List<Offer>) params[0], (Order) params[1]);
        }
    };

    public static final ExtensionManagerOperation buildOfferCodeListForCustomer = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferServiceExtensionHandler) handler).buildOfferCodeListForCustomer((Customer) params[0], (List<OfferCode>) params[1]);
        }
    };

    public static final ExtensionManagerOperation calculatePotentialSavings = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferServiceExtensionHandler) handler).calculatePotentialSavings((PromotableCandidateItemOffer) params[0], (PromotableOrderItem) params[1], (Integer) params[2], (Map<String, Object>) params[3]);
        }
    };

    public static final ExtensionManagerOperation resetPriceDetails = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferServiceExtensionHandler) handler).resetPriceDetails((PromotableOrderItem) params[0]);
        }
    };

    public static final ExtensionManagerOperation applyItemOffer = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferServiceExtensionHandler) handler).applyItemOffer((PromotableOrder) params[0], (PromotableCandidateItemOffer) params[1], (Map<String, Object>) params[2]);
        }
    };

    public static final ExtensionManagerOperation synchronizeAdjustmentsAndPrices = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferServiceExtensionHandler) handler).synchronizeAdjustmentsAndPrices((PromotableOrder) params[0]);
        }
    };

    public static final ExtensionManagerOperation chooseSaleOrRetailAdjustments = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferServiceExtensionHandler) handler).chooseSaleOrRetailAdjustments((PromotableOrder) params[0]);
        }
    };

    public static final ExtensionManagerOperation createOrderItemPriceDetailAdjustment = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferServiceExtensionHandler) handler).createOrderItemPriceDetailAdjustment((ExtensionResultHolder<?>) params[0], (OrderItemPriceDetail) params[1]);
        }
    };

    public static final ExtensionManagerOperation applyAdditionalRuleVariablesForItemOfferEvaluation = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferServiceExtensionHandler) handler).applyAdditionalRuleVariablesForItemOfferEvaluation((PromotableOrderItem) params[0], (HashMap<String, Object>) params[1]);
        }
    };

    public static final ExtensionManagerOperation addAdditionalOffersForCode = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((OfferServiceExtensionHandler) handler).addAdditionalOffersForCode((List<Offer>) params[0], (OfferCode) params[1]);
        }
    };

    public OfferServiceExtensionManager() {
        super(OfferServiceExtensionHandler.class);
    }

    @Override
    public ExtensionResultStatusType applyAdditionalFilters(List<Offer> offers, Order order) {
        return execute(applyAdditionalFilters, offers, order);
    }

    @Override
    public ExtensionResultStatusType buildOfferCodeListForCustomer(Customer customer, List<OfferCode> offerCodes) {
        return execute(buildOfferCodeListForCustomer, customer, offerCodes);
    }

    @Override
    public ExtensionResultStatusType calculatePotentialSavings(PromotableCandidateItemOffer itemOffer, PromotableOrderItem item, int quantity, Map<String, Object> contextMap) {
        return execute(calculatePotentialSavings, itemOffer, item, quantity, contextMap);
    }

    @Override
    public ExtensionResultStatusType resetPriceDetails(PromotableOrderItem item) {
        return execute(resetPriceDetails, item);
    }

    @Override
    public ExtensionResultStatusType applyItemOffer(PromotableOrder order, PromotableCandidateItemOffer itemOffer, Map<String, Object> contextMap) {
        return execute(applyItemOffer, order, itemOffer, contextMap);
    }

    @Override
    public ExtensionResultStatusType synchronizeAdjustmentsAndPrices(PromotableOrder order) {
        return execute(synchronizeAdjustmentsAndPrices, order);
    }

    @Override
    public ExtensionResultStatusType chooseSaleOrRetailAdjustments(PromotableOrder order) {
        return execute(chooseSaleOrRetailAdjustments, order);
    }

    @Override
    public ExtensionResultStatusType createOrderItemPriceDetailAdjustment(ExtensionResultHolder<?> resultHolder, OrderItemPriceDetail itemDetail) {
        return execute(createOrderItemPriceDetailAdjustment, resultHolder, itemDetail);
    }

    @Override
    public ExtensionResultStatusType applyAdditionalRuleVariablesForItemOfferEvaluation(PromotableOrderItem orderItem, HashMap<String, Object> vars) {
        return execute(applyAdditionalRuleVariablesForItemOfferEvaluation, orderItem, vars);
    }

    @Override
    public ExtensionResultStatusType addAdditionalOffersForCode(List<Offer> offers, OfferCode offerCode) {
        return execute(addAdditionalOffersForCode, offers, offerCode);
    }

    @Override
    public boolean isEnabled() {
        //not used - fulfills interface contract
        return true;
    }
}
