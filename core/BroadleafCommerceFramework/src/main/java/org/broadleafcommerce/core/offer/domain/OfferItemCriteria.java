package org.broadleafcommerce.core.offer.domain;

import java.io.Serializable;

public interface OfferItemCriteria extends Serializable {

	public Long getId();

	public void setId(Long id);

	public Integer getReceiveQuantity();

	public void setReceiveQuantity(Integer receiveQuantity);

	public Integer getRequiresQuantity();

	public void setRequiresQuantity(Integer requiresQuantity);

	public String getOrderItemMatchRule();

	public void setOrderItemMatchRule(String orderItemMatchRule);

	public OfferImpl getOffer();

	public void setOffer(OfferImpl offer);
	
}