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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Andre Azzolini (apazzolini), bpolster
 */
public interface OfferServiceExtensionHandler extends ExtensionHandler {
    
    public ExtensionResultStatusType applyAdditionalFilters(List<Offer> offers, Order order);

    /**
     * Allows module extension to add additional offer codes to the list, given the customer
     * @param customer
     * @param offerCodes
     * @return
     */
    public ExtensionResultStatusType buildOfferCodeListForCustomer(Customer customer, List<OfferCode> offerCodes);

    /**
     * Modules may extend the calculatePotentialSavings method.   Once the handlers run, the 
     * contextMap will be checked for an entry with a key of "savings".    If that entry returns a 
     * non-null Money, that value will be returned from the calling method.
     * 
     * Otherwise, the map will be checked for an entry with a key of "quantity".   If a non-null Integer is
     * returned, that value will replace the quantity call in the normal call to calculatePotentialSavings.
     * 
     * This extension is utilized by one or more BLC enterprise modules including Subscription.
     * 
     * @param itemOffer
     * @param item
     * @param quantity
     * @param contextMap
     * @return
     */
    public ExtensionResultStatusType calculatePotentialSavings(PromotableCandidateItemOffer itemOffer,
            PromotableOrderItem item, int quantity, Map<String, Object> contextMap);

    /**
     * Modules may need to clear additional offer details when resetPriceDetails is called.
     * 
     * @param item
     * @return
     */
    public ExtensionResultStatusType resetPriceDetails(PromotableOrderItem item);

    /**
     * Modules may need to extend the applyItemOffer logic
     * 
     * For example, a subscription module might creates future payment adjustments.
     * 
     * The module add an attribute of type Boolean to the contextMap named "stopProcessing" indicating to 
     * the core offer engine that further adjustment processing is not needed. 
     * 
     * @param order
     * @param itemOffer
     * @param contextMap
     * @return
     */
    public ExtensionResultStatusType applyItemOffer(PromotableOrder order, PromotableCandidateItemOffer itemOffer,
            Map<String, Object> contextMap);

    /**
     * Allows a module to amend the data that synchronizes the {@link PromotableOrder} with the {@link Order}
     * @param order
     * @return
     */
    public ExtensionResultStatusType synchronizeAdjustmentsAndPrices(PromotableOrder order);

    /**
     * Allows a module to finalize adjustments.
     * @param order
     * @return
     */
    ExtensionResultStatusType chooseSaleOrRetailAdjustments(PromotableOrder order);

    /**
     * Allows module extensions to add a create a new instance of OrderItemPriceDetailAdjustment.  
     * The module should add the value to the resultHolder.getContextMap() with a key of "OrderItemPriceDetailAdjustment"
     * @param resultHolder
     * @return
     */
    ExtensionResultStatusType createOrderItemPriceDetailAdjustment(ExtensionResultHolder<?> resultHolder,
            OrderItemPriceDetail itemDetail);

    /**
     * Allows a module to append additional rule variables that may be needed for order item evaluation
     * @param orderItem - the promotable order item in consideration
     * @param vars - the rule map
     * @return
     */
    ExtensionResultStatusType applyAdditionalRuleVariablesForItemOfferEvaluation(PromotableOrderItem orderItem,
            HashMap<String, Object> vars);

    /**
     * Allows module extensions to add additional offers for a given offer code.
     * @param offers
     * @param offerCode
     * @return
     */
    ExtensionResultStatusType addAdditionalOffersForCode(List<Offer> offers, OfferCode offerCode);


}
