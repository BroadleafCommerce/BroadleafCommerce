package org.broadleafcommerce.offer.domain;

import java.util.List;

import org.broadleafcommerce.util.money.Money;

public interface ItemOffer {
	public Money getDiscountedPrice();
	public List<Offer> getOffers();
	public int getPriority();
	public Offer getOffer();
}
