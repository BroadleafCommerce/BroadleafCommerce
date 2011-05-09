package org.broadleafcommerce.core.offer.service.candidate;

import java.util.Comparator;

import org.broadleafcommerce.core.offer.domain.CandidateQualifiedOffer;

public class OfferComparator implements Comparator<CandidateQualifiedOffer> {
	
	public static OfferComparator INSTANCE = new OfferComparator();

	public int compare(CandidateQualifiedOffer p1, CandidateQualifiedOffer p2) {
		
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
