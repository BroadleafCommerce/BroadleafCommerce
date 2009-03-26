package org.broadleafcommerce.offer.domain;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.util.money.Money;

public interface CandidateFulfillmentGroupOffer {
	public Money getDiscountedPrice(); //transient, computed
	public FulfillmentGroup getFulfillmentGroup();
	public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup);
	public int getPriority(); // convenience offer.getPriority()
	public Offer getOffer();
	public void setOffer(Offer offer);

}
