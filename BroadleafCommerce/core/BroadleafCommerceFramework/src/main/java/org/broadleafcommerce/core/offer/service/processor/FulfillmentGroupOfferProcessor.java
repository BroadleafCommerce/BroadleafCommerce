package org.broadleafcommerce.core.offer.service.processor;

import java.util.List;

import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;

public interface FulfillmentGroupOfferProcessor extends OrderOfferProcessor {

	public void filterFulfillmentGroupLevelOffer(Order order, List<CandidateFulfillmentGroupOffer> qualifiedFGOffers, List<DiscreteOrderItem> discreteOrderItems, Offer offer);

	public void calculateFulfillmentGroupTotal(Order order);
	
	public boolean applyAllFulfillmentGroupOffers(List<CandidateFulfillmentGroupOffer> qualifiedFGOffers, Order order);
	
}