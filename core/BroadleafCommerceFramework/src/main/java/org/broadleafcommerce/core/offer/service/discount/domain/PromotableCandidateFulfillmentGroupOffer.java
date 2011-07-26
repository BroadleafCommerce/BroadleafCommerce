package org.broadleafcommerce.core.offer.service.discount.domain;

import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.money.Money;

public interface PromotableCandidateFulfillmentGroupOffer {

	public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap();

	public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap);

	public void computeDiscountedPriceAndAmount();

	public void reset();

	public CandidateFulfillmentGroupOffer getDelegate();

	public Money getDiscountedPrice();
	
	public Offer getOffer();
	
	public PromotableFulfillmentGroup getFulfillmentGroup();

    public Money getDiscountedAmount();
}