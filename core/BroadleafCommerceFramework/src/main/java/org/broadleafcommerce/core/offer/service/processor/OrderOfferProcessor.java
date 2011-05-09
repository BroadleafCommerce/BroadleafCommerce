package org.broadleafcommerce.core.offer.service.processor;

import java.util.List;
import java.util.Map;

import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;

public interface OrderOfferProcessor extends BaseProcessor {

	public void filterOrderLevelOffer(Order order, List<CandidateOrderOffer> qualifiedOrderOffers, List<OrderItem> discreteOrderItems, Offer offer);

	public OfferDao getOfferDao();

	public void setOfferDao(OfferDao offerDao);
	
	public Boolean executeExpression(String expression, Map<String, Object> vars);
	
	public boolean couldOfferApplyToOrder(Offer offer, Order order);
	
	public List<CandidateOrderOffer> removeTrailingNotCombinableOrderOffers(List<CandidateOrderOffer> candidateOffers);
	
	public boolean applyAllOrderOffers(List<CandidateOrderOffer> orderOffers, Order order);
	
	public void calculateOrderTotal(Order order);
	
}