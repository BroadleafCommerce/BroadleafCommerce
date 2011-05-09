package org.broadleafcommerce.core.offer.service.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.CandidateQualifiedOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.candidate.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.candidate.OrderItemPriceComparator;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.springframework.stereotype.Service;

@Service("blOrderOfferProcessor")
public class OrderOfferProcessorImpl extends BaseProcessor implements OrderOfferProcessor {

	@Resource(name="blOfferDao")
    protected OfferDao offerDao;
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessor#filterOrderLevelOffer(org.broadleafcommerce.core.order.domain.Order, java.util.List, java.util.List, org.broadleafcommerce.core.offer.domain.Offer)
	 */
	public void filterOrderLevelOffer(Order order, List<CandidateOrderOffer> qualifiedOrderOffers, List<OrderItem> discreteOrderItems, Offer offer) {
		boolean orderLevelQualification = false;
		//Order Qualification
		orderQualification: {
		    if (couldOfferApplyToOrder(offer, order)) {
		    	orderLevelQualification = true;
		    	break orderQualification;
		    }
		    for (OrderItem discreteOrderItem : discreteOrderItems) {
		        if(couldOfferApplyToOrder(offer, order, discreteOrderItem)) {
		        	orderLevelQualification = true;
		        	break orderQualification;
		        }
		    }
		    for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
		        if(couldOfferApplyToOrder(offer, order, fulfillmentGroup)) {
		        	orderLevelQualification = true;
		        	break orderQualification;
		        }
		    }
		}
		//Item Qualification - new for 1.5!
		if (orderLevelQualification) {
			CandidatePromotionItems candidates = couldOfferApplyToOrderItems(offer, discreteOrderItems);
			if (candidates.isMatchedCandidate()) {
				CandidateQualifiedOffer candidateOffer = createCandidateOrderOffer(order, qualifiedOrderOffers, offer);
				candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateItemsMap());
			}
		}
	}
	
	/**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToOrder(Offer offer, Order order) {
        return couldOfferApplyToOrder(offer, order, null, null);
    }

    /**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @param discreteOrderItem
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToOrder(Offer offer, Order order, OrderItem discreteOrderItem) {
        return couldOfferApplyToOrder(offer, order, discreteOrderItem, null);
    }

    /**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @param fulfillmentGroup
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToOrder(Offer offer, Order order, FulfillmentGroup fulfillmentGroup) {
        return couldOfferApplyToOrder(offer, order, null, fulfillmentGroup);
    }

    /**
     * Private method which executes the appliesToOrderRules in the Offer to determine if this offer
     * can be applied to the Order, OrderItem, or FulfillmentGroup.
     *
     * @param offer
     * @param order
     * @param discreteOrderItem
     * @param fulfillmentGroup
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToOrder(Offer offer, Order order, OrderItem discreteOrderItem, FulfillmentGroup fulfillmentGroup) {
        boolean appliesToItem = false;

        if (offer.getAppliesToOrderRules() != null && offer.getAppliesToOrderRules().trim().length() != 0) {

            HashMap<String, Object> vars = new HashMap<String, Object>();
            //vars.put("doMark", Boolean.FALSE); // We never want to mark offers when we are checking if they could apply.
            vars.put("order", order);
            vars.put("offer", offer);
            if (fulfillmentGroup != null) {
                vars.put("fulfillmentGroup", fulfillmentGroup);
            }
            if (discreteOrderItem != null) {
                vars.put("discreteOrderItem", discreteOrderItem);
            }
            Boolean expressionOutcome = executeExpression(offer.getAppliesToOrderRules(), vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToItem = true;
            }
        } else {
            appliesToItem = true;
        }

        return appliesToItem;
    }
    
    protected CandidateOrderOffer createCandidateOrderOffer(Order order, List<CandidateOrderOffer> qualifiedOrderOffers, Offer offer) {
		CandidateOrderOffer candidateOffer = offerDao.createCandidateOrderOffer();
		candidateOffer.setOrder(order);
		candidateOffer.setOffer(offer);
		// Why do we add offers here when we set the sorted list later
		//order.addCandidateOrderOffer(candidateOffer);
		qualifiedOrderOffers.add(candidateOffer);
		
		return candidateOffer;
	}
    
    /**
	 * We were not able to meet all of the ItemCriteria for a promotion, but some of the items were
	 * marked as qualifiers or targets.  This method removes those items from being used as targets or
	 * qualifiers so they are eligible for other promotions.
	 * @param chargeableItems
	 */
	protected void clearAllNonFinalizedQuantities(List<OrderItem> chargeableItems) {
		for(OrderItem chargeableItem : chargeableItems) {
			chargeableItem.clearAllNonFinalizedQuantities();
		}
	}
	
	protected void finalizeQuantities(List<OrderItem> chargeableItems) {
		for(OrderItem chargeableItem : chargeableItems) {
			chargeableItem.finalizeQuantities();
		}
	}
	
	protected void applyItemQualifiersAndTargets(List<OrderItem> discreteOrderItems, CandidateItemOffer itemOffer) {
		Offer promotion = itemOffer.getOffer();
		OrderItemPriceComparator priceComparator = new OrderItemPriceComparator(promotion);
		boolean matchFound = false;
		do {
			boolean atLeastOneCriteriaMatched = false;
			for (OfferItemCriteria itemCriteria : itemOffer.getCandidateQualifiersMap().keySet()) {
				List<OrderItem> chargeableItems = itemOffer.getCandidateQualifiersMap().get(itemCriteria);
				
				// Sort the items so that the highest priced ones are at the top
				Collections.sort(chargeableItems, priceComparator);
				// Calculate the number of qualifiers needed that will not receive the promotion.  
				// These will be reserved first before the target is assigned.
				int extraQualifiersNeeded = itemCriteria.getRequiresQuantity() - itemCriteria.getReceiveQuantity();
				int receiveQtyNeeded = itemCriteria.getReceiveQuantity();
				
				for (OrderItem chargeableItem : chargeableItems) {
					
					// Mark Qualifiers
					if (extraQualifiersNeeded > 0) {
						int itemQtyAvailableToBeUsedAsQualifier = chargeableItem.getQuantityAvailableToBeUsedAsQualifier(promotion);
						if (itemQtyAvailableToBeUsedAsQualifier > 0) {
							int qtyToMarkAsQualifier = Math.min(extraQualifiersNeeded, itemQtyAvailableToBeUsedAsQualifier);
							extraQualifiersNeeded = extraQualifiersNeeded - qtyToMarkAsQualifier;
							atLeastOneCriteriaMatched = true;
							chargeableItem.addPromotionQualifier(itemOffer, itemCriteria, qtyToMarkAsQualifier);
						}
					}
					
					// Mark Targets
					if (receiveQtyNeeded > 0) {
						int itemQtyAvailableToBeUsedAsTarget = chargeableItem.getQuantityAvailableToBeUsedAsTarget(promotion);
						if (itemQtyAvailableToBeUsedAsTarget > 0) {
							int qtyToMarkAsTarget = Math.min(receiveQtyNeeded, itemQtyAvailableToBeUsedAsTarget);
							receiveQtyNeeded = receiveQtyNeeded - qtyToMarkAsTarget;
							atLeastOneCriteriaMatched = true;
							chargeableItem.addPromotionDiscount(itemOffer, itemCriteria, qtyToMarkAsTarget);
						}
					}
					
					if (receiveQtyNeeded == 0 && extraQualifiersNeeded == 0) {
						break;
					}
				}
				
				if (receiveQtyNeeded != 0 && extraQualifiersNeeded != 0) {
					// This ItemCriteria did not match.  Therefore, we need to clear all non-finalized quantities.
					clearAllNonFinalizedQuantities(chargeableItems);
					atLeastOneCriteriaMatched = false;
					break;
				}
			}
			
			// If we made it through the itemCriteria loop, then a match was found
			if (atLeastOneCriteriaMatched) {
				matchFound = true;
				finalizeQuantities(discreteOrderItems);
			}
		} while (matchFound);
		
		Iterator<OrderItem> chargeableItemsIterator = discreteOrderItems.iterator();
		List<OrderItem> splitChargeableItems = new ArrayList<OrderItem>();
		
		while (chargeableItemsIterator.hasNext()) {
			OrderItem chargeableItem = chargeableItemsIterator.next();
			List<OrderItem> splitItems = chargeableItem.split();
			if (splitItems != null && splitItems.size() > 0) {
				// Remove this item from the list
				chargeableItemsIterator.remove();
				splitChargeableItems.addAll(splitItems);
			} 
		}
		
		// Add the split items back to the list
		discreteOrderItems.addAll(splitChargeableItems);
	}

	public OfferDao getOfferDao() {
		return offerDao;
	}

	public void setOfferDao(OfferDao offerDao) {
		this.offerDao = offerDao;
	}
}
