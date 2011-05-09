package org.broadleafcommerce.core.offer.domain;

import java.io.Serializable;

public interface OfferItemCriteria extends Serializable {

	public Long getId();

	public void setId(Long id);

	public Integer getQuantity();

	public void setQuantity(Integer quantity);

	public String getOrderItemMatchRule();

	public void setOrderItemMatchRule(String orderItemMatchRule);

	public Offer getOffer();

	public void setOffer(Offer offer);
	
}