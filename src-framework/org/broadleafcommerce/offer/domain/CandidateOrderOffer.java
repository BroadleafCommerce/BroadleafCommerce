package org.broadleafcommerce.offer.domain;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.util.money.Money;

public interface CandidateOrderOffer {
	public Money getDiscountedPrice(); //transient, computed
	public Order getOrder();
	public void setOrder(Order order);
	public int getPriority(); // convenience offer.getPriority()
	public Offer getOffer();
	public void setOffer(Offer offer);

}
