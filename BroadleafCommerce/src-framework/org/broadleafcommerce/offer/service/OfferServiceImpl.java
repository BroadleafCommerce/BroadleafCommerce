 package org.broadleafcommerce.offer.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanComparator;
import org.broadleafcommerce.offer.domain.CandidateOffer;
import org.broadleafcommerce.offer.domain.ItemOffer;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.offer.domain.StackedOffer;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.pricing.service.PricingService;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.type.OfferDiscountType;
import org.broadleafcommerce.type.OfferType;
import org.broadleafcommerce.util.money.Money;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc
/**
 * The Class OfferServiceImpl.
 */
@Service("offerService")
public class OfferServiceImpl implements OfferService {

	@Resource
	private PricingService pricingService;
	
    /* (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#consumeOffer(org.broadleafcommerce.offer.domain.Offer, org.broadleafcommerce.profile.domain.Customer)
     */
    @Override
    public boolean consumeOffer(Offer offer, Customer customer) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#applyOffersToOrder(java.util.List, org.broadleafcommerce.order.domain.Order)
     */
    @SuppressWarnings("unchecked")
    public void applyOffersToOrder(List<Offer> offers, Order order) {
    	List<Offer> qualifiedOrderOffers = new ArrayList<Offer>();
    	List<ItemOffer> qualifiedItemOffers = new ArrayList<ItemOffer>();
    	order.removeAllOffers();
    	order.setCandaditeOffers(new ArrayList<Offer>());
    	order = pricingService.calculateOrderTotal(order);
    	List<Offer> offersWithValidDates = removeOutOfDateOffers(offers);    	
    	if (offersWithValidDates != null) {
    		//
    		// . Pass One:
    		//
    		for (Offer offer : offersWithValidDates) {
    	    	//
    	    	// . Evaluate all offers and compute their discount amount as if they were the only offer on the order
    	    	//
				if(offer.getType().equals(OfferType.ORDER)){
					// TODO: Determine if order qualifies for offer
					// Assume for now that all orders qualify
					offer = calculateAtomOfferDiscount(offer, order.getSubTotal());
					qualifiedOrderOffers.add(offer);
				}
				if(offer.getType().equals(OfferType.ORDER_ITEM)){
					for (OrderItem orderItem : order.getOrderItems()) {
							// TODO: Determine if orderItem qualifies for offer
							// Assume for now that all orderItems qualify
							if(couldOfferApplyToOrderItem(offer, order, orderItem))
								qualifiedItemOffers.add(new CandidateOffer(offer, orderItem.getRetailPrice(), orderItem.getSalePrice()));
						}
					}				
				if(offer.getType().equals(OfferType.FULLFILLMENT_GROUP)){
					// TODO: Handle Offer calculation for offer type of fullfillment group
				}
			}
	    	//
	    	// . Create a sorted list sorted by priority asc then amount desc
	    	//
    		Collections.sort(qualifiedOrderOffers, new BeanComparator("priority", new BeanComparator("discountedPrice")));
			Collections.sort(qualifiedItemOffers, new BeanComparator("priority", new BeanComparator("discountedPrice")));
			//
			// . Add offers that could be used on the order to the order.candidateOffers and item.candidateOffers lists respectively
			//
			order.setCandaditeOffers(qualifiedOrderOffers);
			for (OrderItem orderItem : order.getOrderItems()) {
				orderItem.setCandidateItemOffers(qualifiedItemOffers);
			}
			//
			// Pass Two:
			//- Iterate through the list above and begin applying ALL of the offers to the order by doing the following:
			for(OrderItem orderItem: order.getOrderItems()) {
				//- Determine the amount that should be discounted for each item
				for (ItemOffer itemOffer : qualifiedItemOffers) {
					//----- If the items sale price is better than the discounted price, don't apply
					if(orderItem.getSalePrice().greaterThan(itemOffer.getDiscountedPrice())){
						// TODO: ----- If the offer requires other items, check to see if the items are still unmarked
						// TODO: ----- If the item itself has been marked by another discount then don't apply this offer unless the offer's applyDiscountToMarkedItems = true (edge case)
						//----- If the item already has a discount and this offer is not-stackable, don't apply
						if(itemOffer.getOffers().size() > 0){							
							for (Offer offer : itemOffer.getOffers()) {
								//----- If the item already has a discount and this offer is stackable, apply on top of the existing offer
								if(offer.isStackable()){
									//----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
									applyItemOffer(orderItem,itemOffer);
								}
							}	
						}else{
							//----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
							applyItemOffer(orderItem,itemOffer);
						}
					}
				}
				
			}
			
			// TODO: How and what do we do with order level offers?
			 

//    		order.setCandaditeOffers(offers);
//    		distributeItemOffers(order, offers);
//
//    		// At this point, each item may have a list of offers which have been sorted such that the
//    		// best offer is the first offer in the list.
//
//    		// Now we need to evaluate the order offers to determine if the order, item, or both should be
//    		// applied.
//
//    		// I see order offers having three variations of "stackable".   
//    		// 		1.  Not-stackable, 
//    		//		2.  Stackable on top of item offers,
//    		// 		3.  Stackable on top of item and order offers
//    		// Based on this, we need to do the following:
//    		// 1. Build list of order offers stackable on top of item offers
//    		// 2. Build list of order offers stackable on top of just order offers
//    		// 3. Build list of order offers stackable that are not stackable
//    		// Next compute the following order totals:
//    		// E1. orderTotalWithItemOffersOnly = xxx;
//    		// E2. orderTotalWithItemAndOrderOffers = xxx;
//    		// E3. orderTotalWithBestOrderOfferOnly = xxx;
//    		//
//    		boolean e1wins=true;
//    		boolean e2wins=false;
//    		boolean e3wins=false;
//    		if (e1wins) {
//    			// TODO: Create ItemAdjustment and add them to each OrderItem for the winning offer on each item
//    		}
//    		if (e2wins) {
//    			// TODO: Create ItemAdjustment and add them to each OrderItem for the winning offer on each item
//    			// TODO: Create ItemAdjustment records for the winning Order offer(s) and add them to each item
//    		}
//    		if (e3wins) {
//    			// TODO: Create ItemAdjustment records for the winning Order offer(s) and add them to each item
//    		}
//    		
//
//    		// now we can apply the first offer for each item which might be a stacked offer
//    		// but first, we need to determine if a non-stacked order offer should be applied instead
//    		// TODO: handle item discount distribution (e.g. applies to maximum of 1 in this order)
//    		// TODO: compute order total with item discounts
//    		// TODO: compute order total without item discounts
//    		// TODO: compute best non-stackable order discount
//    		// TODO: compute best stackable order discount
//    		// TODO: compute best non-stackable order discount that works with item discounts
//    		// TODO: compute best
//    		// TODO: compute order total with discounts plus stackable order discounts
//    		// TODO: compute order total with
//    		//evaluateOffers(order);
//
    	}
    }

    private void applyItemOffer(OrderItem orderItem, ItemOffer itemOffer){
    	// TODO: Apply item offer
		//----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
    }
    
    private Offer calculateAtomOfferDiscount(Offer offer, Money startingValue){
		if(offer.getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)){
			offer.setDiscountPrice(startingValue.subtract(offer.getValue()));
		}
		if(offer.getDiscountType().equals(OfferDiscountType.FIX_PRICE)){
			offer.setDiscountPrice(offer.getValue());
		}
		if(offer.getDiscountType().equals(OfferDiscountType.PERCENT_OFF)){
			offer.setDiscountPrice(startingValue.multiply(Money.toAmount((offer.getValue().divide(new BigDecimal("100"))))));
		}    	
		return offer;
    }
    
    private List<Offer> removeOutOfDateOffers(List<Offer> offers){
    	Date now = new Date();
    	for (Offer offer : offers) {
			if(offer.getStartDate()!= null && offer.getStartDate().after(now)){
				offers.remove(offer);
			}else
			if(offer.getEndDate()!= null && offer.getEndDate().before(now)){
				offers.remove(offer);
			}
		}
    	return offers;
    }
    
    /**
     * Distribute item offers.
     * 
     * @param order the order
     * @param offers the offers
     */
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

    /**
     * Could offer apply to order item.
     * 
     * @param offer the offer
     * @param order the order
     * @param item the item
     * 
     * @return true, if successful
     */
    private boolean couldOfferApplyToOrderItem(Offer offer, Order order, OrderItem item) {
    	boolean appliesTo = false;
    	boolean appliesWhen = false;
    	// TODO: Applies to rule should support any combination of the following expressions:
    	// TODO:      // "all items",
    	// TODO:      // "items whose ${reflected property} (eq, ne, in) ${value(s)}"
    	// TODO:      // "items from category "${category}"
    	// TODO: .........................................
    	if(offer.getAppliesToRules() == null || offer.getAppliesToRules().equals("")){
    		appliesTo = true;
    	}else{
    		// TODO: Evaluate rule to determine if this offer can apply to the given item
    	}
    	
    	// TODO: if offer might apply to this item, then check the when condition
    	// TODO:     "always"
    	// TODO:     "when order contains $(qty} of item whose ${reflected property} (eq, ne, in) ${value}
    	if(offer.getAppliesWhenRules() == null || offer.getAppliesWhenRules().equals("")){
    		appliesWhen = true;
    	}else{
    		// TODO: determine result of applies when rules
    	}
    	return true; // appliesTo && appliesWhen; Just return true for now
    }


    /**
     * Choose item offers.
     * 
     * @param order the order
     */
    protected void chooseItemOffers(Order order) {
    	// Loop through offer
    	// Build list of order items that qualify for offer
    	// Sort list by item amount
    	//
    }



    /* (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#lookupCodeByOffer(org.broadleafcommerce.offer.domain.Offer)
     */
    @Override
    public OfferCode lookupCodeByOffer(Offer offer) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#lookupOfferByCode(java.lang.String)
     */
    @Override
    public Offer lookupOfferByCode(String code) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#lookupValidOffersForSystem(java.lang.String)
     */
    @Override
    public List<Offer> lookupValidOffersForSystem(String system) {
        // TODO Auto-generated method stub
        return null;
    }
}
