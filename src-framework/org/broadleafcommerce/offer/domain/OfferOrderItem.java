package org.broadleafcommerce.offer.domain;

import org.broadleafcommerce.order.domain.OrderItem;

public interface OfferOrderItem {
	public Long getId();
	
	public void setId(Long id);
	
	public Offer getOffer();
	
	public void setOffer(Offer offer);
	
	public OrderItem getOrderItem();
	
	public void setOrderItem(OrderItem orderItem);
}
