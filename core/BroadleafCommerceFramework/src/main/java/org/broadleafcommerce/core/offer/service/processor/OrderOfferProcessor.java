package org.broadleafcommerce.core.offer.service.processor;

import java.util.List;

import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;

public interface OrderOfferProcessor {

	public void filterOrderLevelOffer(Order order, List<CandidateOrderOffer> qualifiedOrderOffers, List<OrderItem> discreteOrderItems, Offer offer);

	public OfferDao getOfferDao();

	public void setOfferDao(OfferDao offerDao);
	
}