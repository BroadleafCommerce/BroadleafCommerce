package org.broadleafcommerce.core.offer.service.processor;

import java.util.List;

import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;

public interface ItemOfferProcessor extends OrderOfferProcessor {

	/**
	 * Review an item level offer against the list of discountable items from the order. If the
	 * offer applies, add it to the qualifiedItemOffers list.
	 * 
	 * @param order the BLC order
	 * @param qualifiedItemOffers the container list for any qualified offers
	 * @param discreteOrderItems the order items to evaluate
	 * @param offer the offer in question
	 */
	public void filterItemLevelOffer(Order order, List<CandidateItemOffer> qualifiedItemOffers, List<OrderItem> discreteOrderItems, Offer offer);

	/**
	 * Private method that takes a list of sorted CandidateItemOffers and determines if each offer can be
	 * applied based on the restrictions (stackable and/or combinable) on that offer.  OrderItemAdjustments
	 * are create on the OrderItem for each applied CandidateItemOffer.  An offer with stackable equals false
	 * cannot be applied to an OrderItem that already contains an OrderItemAdjustment.  An offer with combinable
	 * equals false cannot be applied to an OrderItem if that OrderItem already contains an
	 * OrderItemAdjustment, unless the offer is the same offer as the OrderItemAdjustment offer.
	 *
	 * @param itemOffers a sorted list of CandidateItemOffer
	 * @return true if an OrderItemOffer was applied, otherwise false 
	 */
	public boolean applyAllItemOffers(List<CandidateItemOffer> itemOffers, List<OrderItem> discreteOrderItems, Order order);
	
	public void applyAndCompareOrderAndItemOffers(Order order, List<CandidateOrderOffer> qualifiedOrderOffers, List<CandidateItemOffer> qualifiedItemOffers, List<OrderItem> discreteOrderItems);
	
	public List<OrderItem> filterOffers(Order order, List<Offer> filteredOffers, List<CandidateOrderOffer> qualifiedOrderOffers, List<CandidateItemOffer> qualifiedItemOffers);

}