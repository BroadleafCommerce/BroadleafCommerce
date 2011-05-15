package org.broadleafcommerce.core.offer.service.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.CandidateQualifiedOffer;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.FulfillmentGroupOfferPotential;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.money.Money;
import org.springframework.stereotype.Service;

@Service("blFulfillmentGroupOfferProcessor")
public class FulfillmentGroupOfferProcessorImpl extends OrderOfferProcessorImpl implements FulfillmentGroupOfferProcessor {

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.service.processor.FulfillmentGroupOfferProcessor#filterFulfillmentGroupLevelOffer(org.broadleafcommerce.core.order.domain.Order, java.util.List, java.util.List, org.broadleafcommerce.core.offer.domain.Offer)
	 */
	public void filterFulfillmentGroupLevelOffer(Order order, List<CandidateFulfillmentGroupOffer> qualifiedFGOffers, List<DiscreteOrderItem> discreteOrderItems, Offer offer) {
		for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
			boolean fgLevelQualification = false;
			fgQualification: {
				//handle legacy fields in addition to the 1.5 order rule field
	            if(couldOfferApplyToOrder(offer, order, fulfillmentGroup)) {
	            	fgLevelQualification = true;
	                break fgQualification;
	            }
	            for (OrderItem discreteOrderItem : discreteOrderItems) {
	            	if(couldOfferApplyToOrder(offer, order, discreteOrderItem, fulfillmentGroup)) {
	            		fgLevelQualification = true;
	            		break fgQualification;
	                }
	            }
	    	}
			if (fgLevelQualification) {
				fgLevelQualification = false;
				//handle 1.5 FG field
	            if(couldOfferApplyToFulfillmentGroup(offer, fulfillmentGroup)) {
	            	fgLevelQualification = true;
	            }
			}
			//Item Qualification - new for 1.5!
			if (fgLevelQualification) {
				CandidatePromotionItems candidates = couldOfferApplyToOrderItems(offer, fulfillmentGroup.getDiscountableDiscreteOrderItems());
				if (candidates.isMatchedQualifier()) {
					CandidateQualifiedOffer candidateOffer = createCandidateFulfillmentGroupOffer(offer, qualifiedFGOffers, fulfillmentGroup);
					candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateQualifiersMap());
				}
			}
		}
	}
	
	public void calculateFulfillmentGroupTotal(Order order) {
		for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
			if (fulfillmentGroup.getAdjustmentPrice() != null) {
	            fulfillmentGroup.setShippingPrice(fulfillmentGroup.getAdjustmentPrice());
	        } else if (fulfillmentGroup.getSaleShippingPrice() != null) {
	            fulfillmentGroup.setShippingPrice(fulfillmentGroup.getSaleShippingPrice());
	        } else {
	            fulfillmentGroup.setShippingPrice(fulfillmentGroup.getRetailShippingPrice());
	        }
		}
	}
	
	protected boolean couldOfferApplyToFulfillmentGroup(Offer offer, FulfillmentGroup fulfillmentGroup) {
        boolean appliesToItem = false;
        OfferRule rule = offer.getOfferMatchRules().get(OfferRuleType.FULFILLMENT_GROUP.getType());
        if (rule != null) {
            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("fulfillmentGroup", fulfillmentGroup);
            Boolean expressionOutcome = executeExpression(rule.getMatchRule(), vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToItem = true;
            }
        } else {
            appliesToItem = true;
        }

        return appliesToItem;
    }
	
	protected CandidateFulfillmentGroupOffer createCandidateFulfillmentGroupOffer(Offer offer, List<CandidateFulfillmentGroupOffer> qualifiedFGOffers, FulfillmentGroup fulfillmentGroup) {
		CandidateFulfillmentGroupOffer candidateOffer = offerDao.createCandidateFulfillmentGroupOffer();
		candidateOffer.setFulfillmentGroup(fulfillmentGroup);
		candidateOffer.setOffer(offer);
		fulfillmentGroup.addCandidateFulfillmentGroupOffer(candidateOffer);
		qualifiedFGOffers.add(candidateOffer);
		
		return candidateOffer;
	}
	
	/**
     * Private method that takes a list of sorted CandidateOrderOffers and determines if each offer can be
     * applied based on the restrictions (stackable and/or combinable) on that offer.  OrderAdjustments
     * are create on the Order for each applied CandidateOrderOffer.  An offer with stackable equals false
     * cannot be applied to an Order that already contains an OrderAdjustment.  An offer with combinable
     * equals false cannot be applied to the Order if the Order already contains an OrderAdjustment.
     *
     * @param orderOffers a sorted list of CandidateOrderOffer
     * @param order the Order to apply the CandidateOrderOffers
     * @return true if order offer applied; otherwise false
     */
    @SuppressWarnings("unchecked")
	public boolean applyAllFulfillmentGroupOffers(List<CandidateFulfillmentGroupOffer> qualifiedFGOffers, Order order) {
    	Map<FulfillmentGroupOfferPotential, List<CandidateFulfillmentGroupOffer>> offerMap = new HashMap<FulfillmentGroupOfferPotential, List<CandidateFulfillmentGroupOffer>>();
    	for (CandidateFulfillmentGroupOffer candidate : qualifiedFGOffers) {
    		FulfillmentGroupOfferPotential potential = new FulfillmentGroupOfferPotential();
    		potential.setOffer(candidate.getOffer());
    		if (offerMap.get(potential) == null) {
    			offerMap.put(potential, new ArrayList<CandidateFulfillmentGroupOffer>());
    		}
    		offerMap.get(potential).add(candidate);
    	}
    	List<FulfillmentGroupOfferPotential> potentials = new ArrayList<FulfillmentGroupOfferPotential>();
		for (FulfillmentGroupOfferPotential potential : offerMap.keySet()) {
			for (CandidateFulfillmentGroupOffer candidate : offerMap.get(potential)) {
				potential.setTotalSavings(potential.getTotalSavings().add(candidate.getDiscountAmount()));
				potential.setPriority(candidate.getPriority());
			}
			potentials.add(potential);
		}
		
		// Sort order offers by priority and discount
	    Collections.sort(potentials, new BeanComparator("totalSavings", Collections.reverseOrder()));
	    Collections.sort(potentials, new BeanComparator("priority"));
	    
    	boolean fgOfferApplied = false;
    	for (FulfillmentGroupOfferPotential potential : potentials) {
    		Offer offer = potential.getOffer();
    		if (offer.getTreatAsNewFormat() == null || !offer.getTreatAsNewFormat()) {
    			if ((offer.isStackable()) || !fgOfferApplied) {
    				boolean alreadyContainsNotCombinableOfferAtAnyLevel = order.isNotCombinableOfferAppliedAtAnyLevel();
    				List<CandidateFulfillmentGroupOffer> candidates = offerMap.get(potential);
    				for (CandidateFulfillmentGroupOffer candidate : candidates) {
    					applyFulfillmentGroupOffer(candidate);
    					fgOfferApplied = true;
    				}
    				if (!offer.isCombinableWithOtherOffers() || alreadyContainsNotCombinableOfferAtAnyLevel) {
    					fgOfferApplied = compareAndAdjustFulfillmentGroupOffers(order, fgOfferApplied);
    					if (fgOfferApplied) {
    						break;
    					}
    				}
    			}
    		} else {
    			if (!order.containsNotStackableFulfillmentGroupOffer() || !fgOfferApplied) {
    				boolean alreadyContainsTotalitarianOffer = order.isTotalitarianOfferApplied();
    				List<CandidateFulfillmentGroupOffer> candidates = offerMap.get(potential);
    				for (CandidateFulfillmentGroupOffer candidate : candidates) {
    					applyFulfillmentGroupOffer(candidate);
    					fgOfferApplied = true;
    				}
                	if (
                		(offer.isTotalitarianOffer() != null && offer.isTotalitarianOffer()) ||
                		alreadyContainsTotalitarianOffer
                	) {
                		fgOfferApplied = compareAndAdjustFulfillmentGroupOffers(order, fgOfferApplied);
                		if (fgOfferApplied) {
                    		break;
                    	}
                	} else if (!offer.isCombinableWithOtherOffers()) {
                		break;
                	}
        		}
    		}
    	}
        
        return fgOfferApplied;
    }

	protected boolean compareAndAdjustFulfillmentGroupOffers(Order order, boolean fgOfferApplied) {
		Money regularOrderDiscountShippingTotal = new Money(0D);
		regularOrderDiscountShippingTotal = regularOrderDiscountShippingTotal.add(order.calculateOrderItemsPriceWithoutAdjustments());
		for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
			regularOrderDiscountShippingTotal = regularOrderDiscountShippingTotal.add(fg.getAdjustmentPrice());
		}
		
		Money discountOrderRegularShippingTotal = new Money(0D);
		discountOrderRegularShippingTotal = discountOrderRegularShippingTotal.add(order.getSubTotal());
		for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
			discountOrderRegularShippingTotal = discountOrderRegularShippingTotal.add(fg.getPriceBeforeAdjustments(true));
		}
		
		if (discountOrderRegularShippingTotal.lessThan(regularOrderDiscountShippingTotal)) {
			// order/item offer is better; remove totalitarian fulfillment group offer and process other fg offers
			order.removeAllFulfillmentAdjustments();
		    fgOfferApplied = false;
		} else {
			// totalitarian fg offer is better; remove all order/item offers
			order.removeAllOrderAdjustments();
			order.removeAllItemAdjustments();
			order.getSplitItems().clear();
			order.getSplitItems().addAll(order.getOrderItems());
			mergeSplitItems(order);
		}
		return fgOfferApplied;
	}
	
	protected void applyFulfillmentGroupOffer(CandidateFulfillmentGroupOffer fulfillmentGroupOffer) {
        FulfillmentGroupAdjustment fulfillmentGroupAdjustment = offerDao.createFulfillmentGroupAdjustment();
        fulfillmentGroupAdjustment.init(fulfillmentGroupOffer.getFulfillmentGroup(), fulfillmentGroupOffer.getOffer(), fulfillmentGroupOffer.getOffer().getName());
        //add to adjustment
        fulfillmentGroupOffer.getFulfillmentGroup().addFulfillmentGroupAdjustment(fulfillmentGroupAdjustment);
    }
}
