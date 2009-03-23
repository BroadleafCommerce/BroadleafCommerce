package org.broadleafcommerce.offer.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.broadleafcommerce.util.money.Money;

public class StackedOffer implements ItemOffer {
	private List<Offer> offers;
	private Money discountedPrice;
	private Money salePrice;
	private Money retailPrice;

	public StackedOffer(List<Offer> offers, Money retailPrice, Money salePrice) {
		this.salePrice = salePrice;
		this.retailPrice = retailPrice;
		sortOffers(offers);
		createCandidateOffers(offers);
	}

	private void createCandidateOffers(List<Offer> offers) {
		List<CandidateOffer> candidateOffers = new ArrayList<CandidateOffer>();
		CandidateOffer previousCandidateOffer = null;
		for (int i=0; i< offers.size(); i++) {
			Offer currentOffer = offers.get(i);
			if (i == 0) {
				// The first offer uses the price from the item, subsequent stacked offers
				// use the price from the first offer.
				previousCandidateOffer = new CandidateOffer(currentOffer, retailPrice, salePrice);
			} else {
				previousCandidateOffer = new CandidateOffer(currentOffer, previousCandidateOffer.getDiscountedPrice(), previousCandidateOffer.getDiscountedPrice());
			}
			candidateOffers.add(previousCandidateOffer);
		}
		discountedPrice = previousCandidateOffer.getDiscountedPrice();
	}

	/**
	 * The priority of the first stackable item is the priority of the group.
	 */
	public int getPriority() {
		return offers.get(0).getPriority();
	}

	public Money getDiscountedPrice() {
		return discountedPrice;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	@SuppressWarnings("unchecked")
	private void sortOffers(List<Offer> offers) {
		Collections.sort(offers, new BeanComparator("priority"));
		// TODO: Create map keyed by priority with a list of offers for each priority
		// TODO: if there are more than one item with the same priority, we have to evaluate the result to determine
		//       which one is first
		// TODO: Once we have the whose list, we can build the candidate offers list
	}
	
	public Offer getOffer(){
		return null;
	}
}
