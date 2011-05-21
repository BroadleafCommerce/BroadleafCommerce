/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.core.offer.service.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.CandidateQualifiedOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.ItemOfferComparator;
import org.broadleafcommerce.core.offer.service.discount.OrderItemPriceComparator;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.money.Money;
import org.compass.core.util.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * Filter and apply order item offers.
 * 
 * @author jfischer
 *
 */
@Service("blItemOfferProcessor")
public class ItemOfferProcessorImpl extends OrderOfferProcessorImpl implements ItemOfferProcessor {
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.service.processor.ItemOfferProcessor#filterItemLevelOffer(org.broadleafcommerce.core.order.domain.Order, java.util.List, java.util.List, org.broadleafcommerce.core.offer.domain.Offer)
	 */
	public void filterItemLevelOffer(Order order, List<CandidateItemOffer> qualifiedItemOffers, List<DiscreteOrderItem> discreteOrderItems, Offer offer) {
		boolean isNewFormat = (offer.getQualifyingItemCriteria() != null && offer.getQualifyingItemCriteria().size() > 0) || offer.getTargetItemCriteria() != null;
		boolean itemLevelQualification = false;
		boolean offerCreated = false;
		for (OrderItem discreteOrderItem : discreteOrderItems) {
	    	if(couldOfferApplyToOrder(offer, order, discreteOrderItem)) {
	    		if (!isNewFormat) {
	    			//support legacy offers
	    			CandidateQualifiedOffer candidate = createCandidateItemOffer(qualifiedItemOffers, offer, discreteOrderItem);
	    			if (!candidate.getCandidateTargets().contains(discreteOrderItem)) {
	    				candidate.getCandidateTargets().add(discreteOrderItem);
	    			}
					offerCreated = true;
					continue;
				}
	    		itemLevelQualification = true;
	        	break;
	        }
	    	for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
	            if(couldOfferApplyToOrder(offer, order, discreteOrderItem, fulfillmentGroup)) {
	            	if (!isNewFormat) {
	            		//support legacy offers
	            		CandidateQualifiedOffer candidate = createCandidateItemOffer(qualifiedItemOffers, offer, discreteOrderItem);
	            		if (!candidate.getCandidateTargets().contains(discreteOrderItem)) {
	            			candidate.getCandidateTargets().add(discreteOrderItem);
	            		}
						offerCreated = true;
						continue;
					}
	            	itemLevelQualification = true;
	            	break;
	            }
	        }
	    }
		//Item Qualification - new for 1.5!
		if (itemLevelQualification && !offerCreated) {
			CandidatePromotionItems candidates = couldOfferApplyToOrderItems(offer, discreteOrderItems);
			CandidateQualifiedOffer candidateOffer = null;
			if (candidates.isMatchedQualifier()) {
				//we don't know the final target yet, so put null for the order item for now
				candidateOffer = createCandidateItemOffer(qualifiedItemOffers, offer, null);
				candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateQualifiersMap());
			}
			if (candidates.isMatchedTarget()) {
				if (candidateOffer == null) {
					//we don't know the final target yet, so put null for the order item for now
					candidateOffer = createCandidateItemOffer(qualifiedItemOffers, offer, null);
				}
				for (OrderItem candidateItem : candidates.getCandidateTargets()) {
					CandidateItemOffer itemOffer = ((CandidateItemOffer) candidateOffer).clone();
					itemOffer.setOrderItem(candidateItem);
					candidateItem.getCandidateItemOffers().add(itemOffer);
				}
				candidateOffer.getCandidateTargets().addAll(candidates.getCandidateTargets());
			}
		}
	}
	
	/**
	 * Create a candidate item offer based on the offer in question and a specific order item
	 * 
	 * @param qualifiedItemOffers the container list for candidate item offers
	 * @param offer the offer in question
	 * @param discreteOrderItem the specific order item
	 * @return the candidate item offer
	 */
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
    public boolean applyAllItemOffers(List<CandidateItemOffer> itemOffers, List<DiscreteOrderItem> discreteOrderItems, Order order) {
        // Iterate through the collection of CandiateItemOffers. Remember that each one is an offer that may apply to a
        // particular OrderItem.  Multiple CandidateItemOffers may contain a reference to the same OrderItem object.
        // The same offer may be applied to different Order Items
        //
        // isCombinableWithOtherOffers - not combinable with any offers in the order
        // isStackable - cannot be stack on top of an existing item offer back, other offers can be stack of top of it
        //
    	boolean itemOffersApplied = false;
        int appliedItemOffersCount = 0;
        boolean isLegacyFormat = false;
        for (CandidateItemOffer itemOffer : itemOffers) {
        	int beforeCount = appliedItemOffersCount;
            OrderItem orderItem = itemOffer.getOrderItem();
            if (orderItem != null) {
            	isLegacyFormat = true;
            	appliedItemOffersCount = applyLegacyAdjustments(discreteOrderItems, appliedItemOffersCount, itemOffer, beforeCount, orderItem);
            } else {
            	appliedItemOffersCount = applyAdjustments(discreteOrderItems, order, appliedItemOffersCount, itemOffer, beforeCount);
            }
        }
        if (isLegacyFormat) {
	        appliedItemOffersCount = checkLegacyAdjustments(discreteOrderItems, appliedItemOffersCount);
        } else {
	        appliedItemOffersCount = checkAdjustments(order, appliedItemOffersCount);
        }
        if (appliedItemOffersCount > 0) {
        	itemOffersApplied = true;
        }
        return itemOffersApplied;
    }

	protected int applyAdjustments(List<DiscreteOrderItem> discreteOrderItems, Order order, int appliedItemOffersCount, CandidateItemOffer itemOffer, int beforeCount) {
		boolean notCombinableOfferApplied = false;
		boolean offerApplied = false;
		List<OrderItem> allSplitItems = getAllSplitItems(order);
		for (OrderItem targetItem : allSplitItems) {
			notCombinableOfferApplied = targetItem.isNotCombinableOfferApplied();
			if (!offerApplied) {
				offerApplied = targetItem.isHasOrderItemAdjustments();
			}
			if (notCombinableOfferApplied) {
				break;
			}
		}
		if (
				!notCombinableOfferApplied && (
					(
							(itemOffer.getOffer().isCombinableWithOtherOffers() || itemOffer.getOffer().isTotalitarianOffer() == null || !itemOffer.getOffer().isTotalitarianOffer()) 
							//&& itemOffer.getOffer().isStackable()
					) 
					|| !offerApplied
				)
			) 
		{
	    	applyItemQualifiersAndTargets(discreteOrderItems, itemOffer, order);
	    	allSplitItems = getAllSplitItems(order);
	    	for (OrderItem splitItem : allSplitItems) {
	    		for (PromotionDiscount discount : splitItem.getPromotionDiscounts()) {
	    			if (discount.getPromotion().equals(itemOffer.getOffer())) {
	    				applyOrderItemAdjustment(itemOffer, splitItem);
	    				appliedItemOffersCount++;
	    				break;
	    			}
	    		}
	    	}
		}
		// check if not combinable offer is better than sale price; if no, remove the not combinable offer so 
		// that another offer may be applied to the item
		if ((!itemOffer.getOffer().isCombinableWithOtherOffers() || (itemOffer.getOffer().isTotalitarianOffer() != null && itemOffer.getOffer().isTotalitarianOffer())) && appliedItemOffersCount > beforeCount) { 
			Money adjustmentTotal = new Money(0D);
			Money saleTotal = new Money(0D);
			for (OrderItem splitItem : allSplitItems) {
				adjustmentTotal = adjustmentTotal.add(splitItem.getCurrentPrice().multiply(splitItem.getQuantity()));
				saleTotal = saleTotal.add(splitItem.getPriceBeforeAdjustments(true).multiply(splitItem.getQuantity()));
			}
			if (adjustmentTotal.greaterThanOrEqual(saleTotal)) {
		        // adjustment price is not best price, remove adjustments for this item
				for (OrderItem splitItem : allSplitItems) {
					if (splitItem.isHasOrderItemAdjustments()) {
						appliedItemOffersCount--;
					}
				}
				order.getSplitItems().clear();
		    }
		}
		return appliedItemOffersCount;
	}

	protected int checkAdjustments(Order order, int appliedItemOffersCount) {
		if (appliedItemOffersCount > 0) {
			List<OrderItem> allSplitItems = getAllSplitItems(order);
		    // compare adjustment price to sales price and remove adjustments if sales price is better
		    for (OrderItem splitItem : allSplitItems) {
		        if (splitItem.isHasOrderItemAdjustments()) {
		            Money itemPrice = splitItem.getRetailPrice();
		            if (splitItem.getSalePrice() != null) {
		                itemPrice = splitItem.getSalePrice();
		            }
		            if (splitItem.getAdjustmentPrice().greaterThanOrEqual(itemPrice)) {
		                // adjustment price is not best price, remove adjustments for this item
		                int offersRemoved = splitItem.removeAllAdjustments();
		                appliedItemOffersCount -= offersRemoved;
		            }
		        }
		    }
		    mergeSplitItems(order);
		}
		return appliedItemOffersCount;
	}

	protected int checkLegacyAdjustments(List<DiscreteOrderItem> discreteOrderItems, int appliedItemOffersCount) {
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
		return appliedItemOffersCount;
	}

	protected int applyLegacyAdjustments(List<DiscreteOrderItem> discreteOrderItems, int appliedItemOffersCount, CandidateItemOffer itemOffer, int beforeCount, OrderItem orderItem) {
		//legacy promotion
		if (!orderItem.isNotCombinableOfferApplied()) {
		    if ((itemOffer.getOffer().isCombinableWithOtherOffers() && itemOffer.getOffer().isStackable()) || !orderItem.isHasOrderItemAdjustments()) {
		    	applyOrderItemAdjustment(itemOffer, orderItem);
		        appliedItemOffersCount++;
		    }
		}
		// check if not combinable offer is better than sale price; if no, remove the not combinable offer so 
		// that another offer may be applied to the item
		if (!itemOffer.getOffer().isCombinableWithOtherOffers() && appliedItemOffersCount > beforeCount) { 
			Money adjustmentTotal = new Money(0D);
			Money saleTotal = new Money(0D);
			adjustmentTotal = adjustmentTotal.add(orderItem.getCurrentPrice().multiply(orderItem.getQuantity()));
			saleTotal = saleTotal.add(orderItem.getPriceBeforeAdjustments(true).multiply(orderItem.getQuantity()));
			if (adjustmentTotal.greaterThanOrEqual(saleTotal)) {
		        // adjustment price is not best price, remove adjustments for this item
				orderItem.removeAllAdjustments();
				appliedItemOffersCount--;
		    }
		}
		return appliedItemOffersCount;
	}
    
    protected void applyItemQualifiersAndTargets(List<DiscreteOrderItem> discreteOrderItems, CandidateItemOffer itemOffer, Order order) {
		Offer promotion = itemOffer.getOffer();
		OrderItemPriceComparator priceComparator = new OrderItemPriceComparator(promotion);
		boolean matchFound = false;
		do {
			matchFound = false;
			int totalQualifiersNeeded = 0;
			for (OfferItemCriteria itemCriteria : itemOffer.getCandidateQualifiersMap().keySet()) {
				totalQualifiersNeeded += itemCriteria.getQuantity();
			}
			int receiveQtyNeeded = promotion.getTargetItemCriteria().getQuantity();
			
			checkAll: {
				//boolean atLeastOneCriteriaMatched = false;
				for (OfferItemCriteria itemCriteria : itemOffer.getCandidateQualifiersMap().keySet()) {
					List<OrderItem> chargeableItems = itemOffer.getCandidateQualifiersMap().get(itemCriteria);
					
					// Sort the items so that the highest priced ones are at the top
					Collections.sort(chargeableItems, priceComparator);
					// Calculate the number of qualifiers needed that will not receive the promotion.  
					// These will be reserved first before the target is assigned.
					int qualifierQtyNeeded = itemCriteria.getQuantity();
					
					for (OrderItem chargeableItem : chargeableItems) {
						
						// Mark Qualifiers
						if (qualifierQtyNeeded > 0) {
							int itemQtyAvailableToBeUsedAsQualifier = chargeableItem.getQuantityAvailableToBeUsedAsQualifier(promotion);
							if (itemQtyAvailableToBeUsedAsQualifier > 0) {
								int qtyToMarkAsQualifier = Math.min(qualifierQtyNeeded, itemQtyAvailableToBeUsedAsQualifier);
								qualifierQtyNeeded -= qtyToMarkAsQualifier;
								//atLeastOneCriteriaMatched = true;
								chargeableItem.addPromotionQualifier(itemOffer, itemCriteria, qtyToMarkAsQualifier);
							}
						}
						
						if (qualifierQtyNeeded == 0) {
							totalQualifiersNeeded -= itemCriteria.getQuantity();
							break;
						}
					}
					if (qualifierQtyNeeded != 0) {
						break checkAll;
					}
				}
				checkTargets :{
					List<OrderItem> chargeableItems = itemOffer.getCandidateTargets();
					Collections.sort(chargeableItems, priceComparator);
					for (OrderItem chargeableItem : chargeableItems) {
						// Mark Targets
						if (receiveQtyNeeded > 0) {
							int itemQtyAvailableToBeUsedAsTarget = chargeableItem.getQuantityAvailableToBeUsedAsTarget(promotion);
							if (itemQtyAvailableToBeUsedAsTarget > 0) {
								int qtyToMarkAsTarget = Math.min(receiveQtyNeeded, itemQtyAvailableToBeUsedAsTarget);
								receiveQtyNeeded -= qtyToMarkAsTarget;
								//atLeastOneCriteriaMatched = true;
								chargeableItem.addPromotionDiscount(itemOffer, itemOffer.getOffer().getTargetItemCriteria(), qtyToMarkAsTarget);
							}
						}
						
						if (receiveQtyNeeded == 0) {
							break checkTargets;
						}
					}
				}
			}
			boolean criteriaMatched = true;
			if (receiveQtyNeeded != 0 || totalQualifiersNeeded != 0) {
				// This ItemCriteria did not match.  Therefore, we need to clear all non-finalized quantities.
				for (OfferItemCriteria itemCriteria : itemOffer.getCandidateQualifiersMap().keySet()) {
					List<OrderItem> chargeableItems = itemOffer.getCandidateQualifiersMap().get(itemCriteria);
					clearAllNonFinalizedQuantities(chargeableItems);
				}
				clearAllNonFinalizedQuantities(itemOffer.getCandidateTargets());
				//atLeastOneCriteriaMatched = false;
				criteriaMatched = false;
			}
			
			if (criteriaMatched) {
			//if (atLeastOneCriteriaMatched) {
				matchFound = true;
				finalizeQuantities(discreteOrderItems);
			}
			//This promotion may be able to be applied multiple times if there is enough
			//product quantity in the order. Continue to loop through the order until
			//there are no more matches
		} while (matchFound);
		
		if (order.getSplitItems().size() == 0) {
			initializeSplitItems(order, order.getOrderItems());
		}
		List<OrderItem> allSplitItems = getAllSplitItems(order);
		for (OrderItem chargeableItem : allSplitItems) {
			if (itemOffer.getCandidateTargets().contains(chargeableItem)) {
				List<OrderItem> splitItems = chargeableItem.split();
				if (splitItems != null && splitItems.size() > 0) {
					// Remove this item from the list
					List<OrderItem> temp = order.searchSplitItems(chargeableItem);
					if (!CollectionUtils.isEmpty(temp)) {
						temp.remove(chargeableItem);
						temp.addAll(splitItems);
					}
				}
			} 
		}
	}

    /**
     * Private method used by applyAllItemOffers to create an OrderItemAdjustment from a CandidateItemOffer
     * and associates the OrderItemAdjustment to the OrderItem.
     *
     * @param itemOffer a CandidateItemOffer to apply to an OrderItem
     */
    protected void applyOrderItemAdjustment(CandidateItemOffer itemOffer, OrderItem orderItem) {
        OrderItemAdjustment itemAdjustment = offerDao.createOrderItemAdjustment();
        itemAdjustment.init(orderItem, itemOffer.getOffer(), itemOffer.getOffer().getName());
        //add to adjustment
        orderItem.addOrderItemAdjustment(itemAdjustment); //This is how we can tell if an item has been discounted
    }
    
    public List<DiscreteOrderItem> filterOffers(Order order, List<Offer> filteredOffers, List<CandidateOrderOffer> qualifiedOrderOffers, List<CandidateItemOffer> qualifiedItemOffers) {
		// set order subtotal price to total item price without adjustments
    	order.setSubTotal(order.calculateOrderItemsFinalPrice(true));
		List<DiscreteOrderItem> discreteOrderItems = order.getDiscountableDiscreteOrderItems();
		for (Offer offer : filteredOffers) {
			OrderItemPriceComparator priceComparator = new OrderItemPriceComparator(offer);
			// Sort the items so that the highest priced ones are at the top
			Collections.sort(discreteOrderItems, priceComparator);
			
		    if(offer.getType().equals(OfferType.ORDER)){
		    	filterOrderLevelOffer(order, qualifiedOrderOffers, discreteOrderItems, offer);
		    } else if(offer.getType().equals(OfferType.ORDER_ITEM)){
		    	filterItemLevelOffer(order, qualifiedItemOffers, discreteOrderItems, offer);
		    }
		}
		return discreteOrderItems;
	}
    
    @SuppressWarnings("unchecked")
	public void applyAndCompareOrderAndItemOffers(Order order, List<CandidateOrderOffer> qualifiedOrderOffers, List<CandidateItemOffer> qualifiedItemOffers, List<DiscreteOrderItem> discreteOrderItems) {
		if (!qualifiedItemOffers.isEmpty()) {
		    // Sort order item offers by priority and total discount
			Collections.sort(qualifiedItemOffers, ItemOfferComparator.INSTANCE);
			applyAllItemOffers(qualifiedItemOffers, discreteOrderItems, order);
		}
		
		if (!qualifiedOrderOffers.isEmpty()) {
		    // Sort order offers by priority and discount
		    Collections.sort(qualifiedOrderOffers, new BeanComparator("discountAmount", Collections.reverseOrder()));
		    Collections.sort(qualifiedOrderOffers, new BeanComparator("priority"));
		    qualifiedOrderOffers = removeTrailingNotCombinableOrderOffers(qualifiedOrderOffers);
		    applyAllOrderOffers(qualifiedOrderOffers, order);
		}
		
		compileOrderTotal(order);
		
		if (!qualifiedOrderOffers.isEmpty() && !qualifiedItemOffers.isEmpty()) {
		    List<CandidateOrderOffer> finalQualifiedOrderOffers = new ArrayList<CandidateOrderOffer>();
		    order.removeAllOrderAdjustments();
		    for (CandidateOrderOffer condidateOrderOffer : qualifiedOrderOffers) {
		    	// recheck the list of order offers and verify if they still apply with the new subtotal
		    	/*
		    	 * Note - there is an edge case possibility where this logic would miss an order promotion
		    	 * that had a subtotal requirement that was missed because of item deductions, but without
		    	 * the item deductions, the order promotion would have been included and ended up giving the 
		    	 * customer a better deal than the item deductions.
		    	 */
		        if (couldOfferApplyToOrder(condidateOrderOffer.getOffer(), order)) {
		            finalQualifiedOrderOffers.add(condidateOrderOffer);
		        }
		    }

		    // Sort order offers by priority and discount
		    Collections.sort(finalQualifiedOrderOffers, new BeanComparator("discountedPrice"));
		    Collections.sort(finalQualifiedOrderOffers, new BeanComparator("priority"));
		    if (!finalQualifiedOrderOffers.isEmpty()) {
		        applyAllOrderOffers(finalQualifiedOrderOffers, order);
		    }  
		}
	}
}
