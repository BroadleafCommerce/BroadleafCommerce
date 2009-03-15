package org.broadleafcommerce.offer.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.broadleafcommerce.offer.domain.CandidateOffer;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.offer.domain.StackedOffer;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.type.OfferType;
import org.springframework.stereotype.Service;

@Service("offerService")
public class OfferServiceImpl implements OfferService {

    @Override
    public boolean consumeOffer(Offer offer, Customer customer) {
        // TODO Auto-generated method stub
        return false;
    }

    public void applyOffersToOrder(List<Offer> offers, Order order) {
    	order.removeAllOffers();
    	if (offers != null) {
    		order.setCandaditeOffers(offers);
    		distributeItemOffers(order, offers);

    		// At this point, each item may have a list of offers which have been sorted such that the
    		// best offer is the first offer in the list.

    		// Now we need to evaluate the order offers to determine if the order, item, or both should be
    		// applied.

    		// I see order offers having three variations of "stackable".   1.  Not-stackable, 2.  Stackable on top of item offers,
    		// 3.  Stackable on top of item and order offers
    		// Based on this, we need to do the following:
    		// 1. Build list of order offers stackable on top of item offers
    		// 2. Build list of order offers stackable on top of just order offers
    		// 3. Build list of order offers stackable that are not stackable
    		// Next compute the following order totals:
    		// E1. orderTotalWithItemOffersOnly = xxx;
    		// E2. orderTotalWithItemAndOrderOffers = xxx;
    		// E3. orderTotalWithBestOrderOfferOnly = xxx;
    		//
    		boolean e1wins=true;
    		boolean e2wins=false;
    		boolean e3wins=false;
    		if (e1wins) {
    			// TODO: Create ItemAdjustment and add them to each OrderItem for the winning offer on each item
    		}
    		if (e2wins) {
    			// TODO: Create ItemAdjustment and add them to each OrderItem for the winning offer on each item
    			// TODO: Create ItemAdjustment records for the winning Order offer(s) and add them to each item
    		}
    		if (e3wins) {
    			// TODO: Create ItemAdjustment records for the winning Order offer(s) and add them to each item
    		}


    		// now we can apply the first offer for each item which might be a stacked offer
    		// but first, we need to determine if a non-stacked order offer should be applied instead
    		// TODO: handle item discount distribution (e.g. applies to maximum of 1 in this order)
    		// TODO: compute order total with item discounts
    		// TODO: compute order total without item discounts
    		// TODO: compute best non-stackable order discount
    		// TODO: compute best stackable order discount
    		// TODO: compute best non-stackable order discount that works with item discounts
    		// TODO: compute best
    		// TODO: compute order total with discounts plus stackable order discounts
    		// TODO: compute order total with
    		//evaluateOffers(order);

    	}
    }

    @SuppressWarnings("unchecked")
	private void distributeItemOffers(Order order, List<Offer> offers) {
    	for (OrderItem item : order.getOrderItems()) {
    		List<Offer> stackableOffersList = null;
			for (Offer offer : offers) {
				if (OfferType.ORDER_ITEM.equals(offer.getType())) {
					if (couldOfferApplyToOrderItem(offer, order, item)) {
						if (offer.isStackable()) {
							if (stackableOffersList == null) {
								stackableOffersList = new ArrayList<Offer>();
							}
							stackableOffersList.add(offer);
						} else {
							CandidateOffer candidateOffer = new CandidateOffer(offer, item.getRetailPrice(), item.getSalePrice());
							item.addCandidateItemOffer(candidateOffer);
						}
					}
				}
			}
			if (stackableOffersList != null) {
				StackedOffer stackedOffer = new StackedOffer(stackableOffersList, item.getRetailPrice(), item.getSalePrice());
				item.addCandidateItemOffer(stackedOffer);
			}

			// Sorts offers by priority then discounted price
			Collections.sort(item.getCandidateItemOffers(), new BeanComparator("priority", new BeanComparator("discountedPrice")));
		}
    }

    private boolean couldOfferApplyToOrderItem(Offer offer, Order order, OrderItem item) {
    	// TODO: Evaluate rule to determine if this offer can apply to the given item
    	// TODO: Applies to rule should support any combination of the following expressions:
    	// TODO:      // "all items",
    	// TODO:      // "items whose ${reflected property} (eq, ne, in) ${value(s)}"
    	// TODO:      // "items from category "${category}"
    	// TODO: .........................................
    	// TODO: if offer might apply to this item, then check the when condition
    	// TODO:     "always"
    	// TODO:     "when order contains $(qty} of item whose ${reflected property} (eq, ne, in) ${value}
    	return true;
    }


    protected void chooseItemOffers(Order order) {
    	// Loop through offer
    	// Build list of order items that qualify for offer
    	// Sort list by item amount
    	//
    }



    @Override
    public OfferCode lookupCodeByOffer(Offer offer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Offer lookupOfferByCode(String code) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Offer> lookupValidOffersForSystem(String system) {
        // TODO Auto-generated method stub
        return null;
    }
}
