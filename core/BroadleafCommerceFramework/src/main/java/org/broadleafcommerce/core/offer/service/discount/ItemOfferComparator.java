package org.broadleafcommerce.core.offer.service.discount;

import java.util.Comparator;

import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;

public class ItemOfferComparator implements Comparator<CandidateItemOffer> {
	
	public static ItemOfferComparator INSTANCE = new ItemOfferComparator();

	public int compare(CandidateItemOffer p1, CandidateItemOffer p2) {
		
		Integer priority1 = p1.getPriority();
		Integer priority2 = p2.getPriority();
		
		int result = priority1.compareTo(priority2);
		
		if (result == 0) {
			// highest potential savings wins
			return p2.getPotentialSavings().compareTo(p1.getPotentialSavings());
		} else {
			return result;
		}
	}

}
