package org.broadleafcommerce.offer.domain;

import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.util.money.Money;

public interface CandidateItemOffer {
	public Money getDiscountedPrice(); //transient, computed
	public OrderItem getOrderItem();
	public void setOrderItem(OrderItem orderItem);
	public int getPriority(); // convenience offer.getPriority()
	public Offer getOffer();
	public void setOffer(Offer offer);
}
