package org.broadleafcommerce.core.offer.service.candidate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.order.domain.OrderItem;

public class CandidatePromotionItems {
	
	private HashMap<OfferItemCriteria, List<OrderItem>> candidateItemsMap = new HashMap<OfferItemCriteria, List<OrderItem>>();
	private boolean isMatchedCandidate = false;
	
	public void addCandidateItem(OfferItemCriteria criteria, OrderItem item) {
		List<OrderItem> itemList = candidateItemsMap.get(criteria);
		if (itemList == null) {
			itemList = new ArrayList<OrderItem>();
			candidateItemsMap.put(criteria, itemList);
		}
		itemList.add(item);
	}

	public boolean isMatchedCandidate() {
		return isMatchedCandidate;
	}

	public void setMatchedCandidate(boolean isMatchedCandidate) {
		this.isMatchedCandidate = isMatchedCandidate;
	}

	public HashMap<OfferItemCriteria, List<OrderItem>> getCandidateItemsMap() {
		return candidateItemsMap;
	}

}
