package org.broadleafcommerce.core.offer.service.discount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.order.domain.OrderItem;

public class CandidatePromotionItems {
	
	protected HashMap<OfferItemCriteria, List<OrderItem>> candidateQualifiersMap = new HashMap<OfferItemCriteria, List<OrderItem>>();
	protected boolean isMatchedQualifier = false;
	protected List<OrderItem> candidateTargets = new ArrayList<OrderItem>();
	protected boolean isMatchedTarget = false;
	
	public void addQualifier(OfferItemCriteria criteria, OrderItem item) {
		List<OrderItem> itemList = candidateQualifiersMap.get(criteria);
		if (itemList == null) {
			itemList = new ArrayList<OrderItem>();
			candidateQualifiersMap.put(criteria, itemList);
		}
		itemList.add(item);
	}

	public boolean isMatchedQualifier() {
		return isMatchedQualifier;
	}

	public void setMatchedQualifier(boolean isMatchedCandidate) {
		this.isMatchedQualifier = isMatchedCandidate;
	}

	public HashMap<OfferItemCriteria, List<OrderItem>> getCandidateQualifiersMap() {
		return candidateQualifiersMap;
	}
	
	public void addTarget(OrderItem item) {
		candidateTargets.add(item);
	}

	public boolean isMatchedTarget() {
		return isMatchedTarget;
	}

	public void setMatchedTarget(boolean isMatchedCandidate) {
		this.isMatchedTarget = isMatchedCandidate;
	}

	public List<OrderItem> getCandidateTargets() {
		return candidateTargets;
	}

}
