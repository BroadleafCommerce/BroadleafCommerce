package org.broadleafcommerce.core.offer.domain;

import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.money.Money;

public interface CandidateQualifiedOffer extends CandidateOffer {

	public HashMap<OfferItemCriteria, List<OrderItem>> getCandidateQualifiersMap();

	public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<OrderItem>> candidateItemsMap);

	public Money getPotentialSavings();
	
	public Money calculateSavingsForOrderItem(OrderItem chgItem, int qtyToReceiveSavings);
	
}