package org.broadleafcommerce.core.offer.service.discount.domain;

import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;

public interface PromotableCandidateOrderOffer {

	public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap();

	public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap);

	public void computeDiscountedPriceAndAmount();

	public void reset();
	
	public CandidateOrderOffer getDelegate();
	
	public PromotableOrder getOrder();
	
	public Offer getOffer();
	
}