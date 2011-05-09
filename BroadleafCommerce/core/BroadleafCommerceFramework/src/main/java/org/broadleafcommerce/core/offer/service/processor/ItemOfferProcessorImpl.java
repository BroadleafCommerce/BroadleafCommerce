package org.broadleafcommerce.core.offer.service.processor;

import java.util.List;

import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateQualifiedOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.candidate.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.candidate.PromotionDiscount;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.money.Money;
import org.springframework.stereotype.Service;

@Service("blItemOfferProcessor")
public class ItemOfferProcessorImpl extends OrderOfferProcessorImpl implements ItemOfferProcessor {
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor#filterItemLevelOffer(org.broadleafcommerce.core.order.domain.Order, java.util.List, java.util.List, org.broadleafcommerce.core.offer.domain.Offer)
	 */
	public void filterItemLevelOffer(Order order, List<CandidateItemOffer> qualifiedItemOffers, List<OrderItem> discreteOrderItems, Offer offer) {
		boolean isBogo = offer.getQualifyingItemCriteria() != null && offer.getQualifyingItemCriteria().size() > 0;
		if (isBogo) {
			boolean itemLevelQualification = false;
			for (OrderItem discreteOrderItem : discreteOrderItems) {
		    	if(couldOfferApplyToOrder(offer, order, discreteOrderItem)) {
		    		itemLevelQualification = true;
		        	break;
		        }
		    	for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
		            if(couldOfferApplyToOrder(offer, order, discreteOrderItem, fulfillmentGroup)) {
		            	itemLevelQualification = true;
		            	break;
		            }
		        }
		    }
			//Item Qualification - new for 1.5!
			if (itemLevelQualification) {
				CandidatePromotionItems candidates = couldOfferApplyToOrderItems(offer, discreteOrderItems);
				if (candidates.isMatchedCandidate()) {
					//we don't know the target yet, so put null for the order item for now
					CandidateQualifiedOffer candidateOffer = createCandidateItemOffer(qualifiedItemOffers, offer, null);
					candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateItemsMap());
				}
			}
		} else {
			//support legacy offers
		    for (OrderItem discreteOrderItem : discreteOrderItems) {
		    	checkSubItems: {
		        	if(couldOfferApplyToOrder(offer, order, discreteOrderItem)) {
		                createCandidateItemOffer(qualifiedItemOffers, offer, discreteOrderItem);
		                break checkSubItems;
		            }
		        	for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
		                if(couldOfferApplyToOrder(offer, order, discreteOrderItem, fulfillmentGroup)) {
		                	createCandidateItemOffer(qualifiedItemOffers, offer, discreteOrderItem);
		                	break checkSubItems;
		                }
		            }
		    	}
		    }
		}
	}
	
	protected CandidateItemOffer createCandidateItemOffer(List<CandidateItemOffer> qualifiedItemOffers, Offer offer, OrderItem discreteOrderItem) {
		CandidateItemOffer candidateOffer = offerDao.createCandidateItemOffer();
		candidateOffer.setOrderItem(discreteOrderItem);
		candidateOffer.setOffer(offer);
		if (discreteOrderItem != null) {
			discreteOrderItem.addCandidateItemOffer(candidateOffer);
		}
		qualifiedItemOffers.add(candidateOffer);
		
		return candidateOffer;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor#applyAllItemOffers(java.util.List, java.util.List)
	 */
    public boolean applyAllItemOffers(List<CandidateItemOffer> itemOffers, List<OrderItem> discreteOrderItems) {
        // Iterate through the collection of CandiateItemOffers. Remember that each one is an offer that may apply to a
        // particular OrderItem.  Multiple CandidateItemOffers may contain a reference to the same OrderItem object.
        // The same offer may be applied to different Order Items
        //
        // isCombinableWithOtherOffers - not combinable with any offers in the order
        // isStackable - cannot be stack on top of an existing item offer back, other offers can be stack of top of it
        //
    	boolean itemOffersApplied = false;
        int appliedItemOffersCount = 0;
        for (CandidateItemOffer itemOffer : itemOffers) {
            OrderItem orderItem = itemOffer.getOrderItem();
            if (!orderItem.isNotCombinableOfferApplied()) {
                if ((itemOffer.getOffer().isCombinableWithOtherOffers() && itemOffer.getOffer().isStackable()) || !orderItem.isHasOrderItemAdjustments()) {
                	boolean targetsApplied = false;
                	applyItemQualifiersAndTargets(discreteOrderItems, itemOffer);
                	for (OrderItem discreteOrderItem : discreteOrderItems) {
                		for (PromotionDiscount discount : discreteOrderItem.getPromotionDiscounts()) {
                			if (discount.getPromotion().equals(itemOffer.getOffer())) {
                				applyOrderItemOffer(itemOffer);
                				targetsApplied = true;
                				break;
                			}
                		}
                	}
                	// check if not combinable offer is better than sale price; if no, remove the not combinable offer so 
                	// that another offer may be applied to the item
                	if (!targetsApplied) {
                		applyOrderItemOffer(itemOffer);
                	}
                    if (!itemOffer.getOffer().isCombinableWithOtherOffers()) {                    
                        Money itemPrice = itemOffer.getOrderItem().getRetailPrice();
                        if (itemOffer.getOrderItem().getSalePrice() != null) {
                            itemPrice = itemOffer.getOrderItem().getSalePrice();
                        }
                        if (itemOffer.getOrderItem().getAdjustmentPrice().greaterThanOrEqual(itemPrice)) {
                            // adjustment price is not best price, remove adjustments for this item
                            itemOffer.getOrderItem().removeAllAdjustments();
                            appliedItemOffersCount--;
                        }
                    }
                    appliedItemOffersCount++;
                }
            } 
        }
        if (appliedItemOffersCount > 0) {
            // compare adjustment price to sales price and remove adjustments if sales price is better
            for (OrderItem discreteOrderItem : discreteOrderItems) {
                if (discreteOrderItem.getAdjustmentPrice() != null) {
                    Money itemPrice = discreteOrderItem.getRetailPrice();
                    if (discreteOrderItem.getSalePrice() != null) {
                        itemPrice = discreteOrderItem.getSalePrice();
                    }
                    if (discreteOrderItem.getAdjustmentPrice().greaterThanOrEqual(itemPrice)) {
                        // adjustment price is not best price, remove adjustments for this item
                        int offersRemoved = discreteOrderItem.removeAllAdjustments();
                        appliedItemOffersCount = appliedItemOffersCount - offersRemoved;
                    }
                }
            }
        }
        if (appliedItemOffersCount > 0) {
        	itemOffersApplied = true;
        }
        return itemOffersApplied;
    }

    /**
     * Private method used by applyAllItemOffers to create an OrderItemAdjustment from a CandidateItemOffer
     * and associates the OrderItemAdjustment to the OrderItem.
     *
     * @param itemOffer a CandidateItemOffer to apply to an OrderItem
     */
    protected void applyOrderItemOffer(CandidateItemOffer itemOffer) {
        OrderItemAdjustment itemAdjustment = offerDao.createOrderItemAdjustment();
        itemAdjustment.init(itemOffer.getOrderItem(), itemOffer.getOffer(), itemOffer.getOffer().getName());
        //add to adjustment
        itemOffer.getOrderItem().addOrderItemAdjustment(itemAdjustment); //This is how we can tell if an item has been discounted
    }
}
