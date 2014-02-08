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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferMarkTargets;
import org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessorImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;
import org.broadleafcommerce.core.order.domain.dto.OrderItemHolder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class serves to allow reuse of logic between the core offer service and related offer service extensions.
 * 
 * Changes here likely affect other modules including advanced offer and subscription.
 * 
 * @author bpolster
 *
 */
public interface OfferServiceUtilities {

    /**
     * Used in {@link ItemOfferProcessorImpl#applyItemQualifiersAndTargets(PromotableCandidateItemOffer, PromotableOrder)} 
     * Allow for customized sorting for which qualifier items should be attempted to be used first for a promotion. 
     * 
     * Default behavior is to sort descending, so higher-value items are attempted to be discounted first.
    */
    void sortTargetItemDetails(List<PromotableOrderItemPriceDetail> itemPriceDetails, boolean applyToSalePrice);

    /**
     * Used in {@link ItemOfferProcessorImpl#applyItemQualifiersAndTargets(PromotableCandidateItemOffer, PromotableOrder)} 
     * Allow for customized sorting for which qualifier items should be attempted to be used first for a promotion. 
     * 
     * Default behavior is to sort descending, so higher-value items are attempted to be qualified first.
    */
    void sortQualifierItemDetails(List<PromotableOrderItemPriceDetail> itemPriceDetails, boolean applyToSalePrice);

    /**
     * Given an orderItem, finds the top most parent order item.  
     * @param relatedQualifier
     * @return
     */
    OrderItem findRelatedQualifierRoot(OrderItem relatedQualifier);

    /**
     * Return false if a totalitarian or non-combinable offer has already been applied or if this offer is 
     * totalitarian or non-combinable  and this order already has adjustments applied.
     *      
     * @param order
     * @param details
     * @return
     */
    public boolean itemOfferCanBeApplied(PromotableCandidateItemOffer itemOffer,
            List<PromotableOrderItemPriceDetail> details);

    /**
     * Returns the number of qualifiers marked for the passed in itemCriteria    
     * @param itemOffer
     * @param itemCriteria
     * @param priceDetails
     * @return
     */
    int markQualifiersForCriteria(PromotableCandidateItemOffer itemOffer, OfferItemCriteria itemCriteria,
            List<PromotableOrderItemPriceDetail> priceDetails);
    
    /**
     * Returns the number of targets marked for the passed in itemCriteria
     *     
     * @param itemOffer
     * @param relatedQualifier
     * @param checkOnly
     * @param promotion
     * @param relatedQualifierRoot
     * @param itemCriteria
     * @param priceDetails
     * @param targetQtyNeeded
     * @return
     */
    int markTargetsForCriteria(PromotableCandidateItemOffer itemOffer, OrderItem relatedQualifier, boolean checkOnly,
            Offer promotion, OrderItem relatedQualifierRoot, OfferItemCriteria itemCriteria,
            List<PromotableOrderItemPriceDetail> priceDetails, int targetQtyNeeded);

    /**
     * Returns the number of targets marked for the passed in itemCriteria
     * 
     * @param itemOffer
     * @param order
     * @param orderItemHolder
     * @param itemCriteria
     * @param priceDetails
     * @return
     */
    int markRelatedQualifiersAndTargetsForItemCriteria(PromotableCandidateItemOffer itemOffer, PromotableOrder order,
            OrderItemHolder orderItemHolder, OfferItemCriteria itemCriteria,
            List<PromotableOrderItemPriceDetail> priceDetails, ItemOfferMarkTargets itemOfferMarkTargets);

    /**
     * Takes in a list of {@link PromotableOrderItemPriceDetail}s  and applies adjustments for all of the 
     * discounts that match the passed in offer.
     * 
     * @param itemOffer
     * @param itemPriceDetails
     */
    void applyAdjustmentsForItemPriceDetails(PromotableCandidateItemOffer itemOffer,
            List<PromotableOrderItemPriceDetail> itemPriceDetails);
      
    /**
     * Used by applyAdjustments to create an OrderItemAdjustment from a CandidateOrderOffer
     * and associates the OrderItemAdjustment to the OrderItem.
     *
     * @param orderOffer a CandidateOrderOffer to apply to an Order
     */
    void applyOrderItemAdjustment(PromotableCandidateItemOffer itemOffer, PromotableOrderItemPriceDetail itemPriceDetail);

    /**
     * Builds the list of order-items at the level they are being priced which includes splitting bundles that are 
     * being priced at the item level.
     * 
     * @param order
     * @return
     */
    List<OrderItem> buildOrderItemList(Order order);

    /**
     * Builds a map from orderItem to promotableOrderItem.
     * @param promotableOrder
     * @return
     */
    Map<OrderItem, PromotableOrderItem> buildPromotableItemMap(PromotableOrder promotableOrder);

    /**
     * Builds a map from itemDetails for adjustment processing.
     * @param itemDetail
     * @return
     */
    Map<Long, OrderItemPriceDetailAdjustment> buildItemDetailAdjustmentMap(OrderItemPriceDetail itemDetail);


    /**
     * Updates the passed in price detail and its associated adjustments.
     * @param itemDetail
     * @param promotableDetail
     */
    void updatePriceDetail(OrderItemPriceDetail itemDetail, PromotableOrderItemPriceDetail promotableDetail);

    /**
     * Removes price details from the iterator that are contained in the passed in map.
     * @param unmatchedDetailsMap
     * @param pdIterator
     */
    void removeUnmatchedPriceDetails(Map<Long, ? extends OrderItemPriceDetail> unmatchedDetailsMap,
            Iterator<? extends OrderItemPriceDetail> pdIterator);
    
    /**
     * Removes qualifiers from the iterator that are contained in the passed in map.
     * @param unmatchedQualifiersMap
     * @param qIterator
     */
    void removeUnmatchedQualifiers(Map<Long, ? extends OrderItemQualifier> unmatchedQualifiersMap,
            Iterator<? extends OrderItemQualifier> qIterator);
}
