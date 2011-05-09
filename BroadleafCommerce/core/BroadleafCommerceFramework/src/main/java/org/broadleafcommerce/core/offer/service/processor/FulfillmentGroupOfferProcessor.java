package org.broadleafcommerce.core.offer.service.processor;

import java.util.List;

import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;

public interface FulfillmentGroupOfferProcessor extends OrderOfferProcessor {

	public void filterFulfillmentGroupLevelOffer(Order order, List<CandidateFulfillmentGroupOffer> qualifiedFGOffers, List<OrderItem> discreteOrderItems, Offer offer);

}