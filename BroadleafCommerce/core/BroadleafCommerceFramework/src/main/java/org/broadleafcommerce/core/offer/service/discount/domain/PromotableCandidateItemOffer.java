package org.broadleafcommerce.core.offer.service.discount.domain;

import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.money.Money;

public interface PromotableCandidateItemOffer {

	public HashMap<OfferItemCriteria, List<PromotableOrderItem>> getCandidateQualifiersMap();

	public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<PromotableOrderItem>> candidateItemsMap);

	public List<PromotableOrderItem> getCandidateTargets();

	public void setCandidateTargets(List<PromotableOrderItem> candidateTargets);

	public Money calculateSavingsForOrderItem(PromotableOrderItem orderItem, int qtyToReceiveSavings);

	public Money getPotentialSavings();

	public CandidateItemOffer getDelegate();
	
	public void reset();
	
	public Money calculatePotentialSavings();
	
	public int calculateMaximumNumberOfUses();
	
	public int calculateMaxUsesForItemCriteria(OfferItemCriteria itemCriteria, Offer promotion);
	
	public void setOrderItem(PromotableOrderItem orderItem);
	
	public PromotableCandidateItemOffer clone();
	
	public int getPriority();
	
	public Offer getOffer();
	
	public void setOffer(Offer offer);
	
	public PromotableOrderItem getOrderItem();
	
}