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

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupType;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.hibernate.tool.hbm2x.StringUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author jfischer
 *
 */
public abstract class AbstractBaseProcessor implements BaseProcessor {

	private static final Log LOG = LogFactory.getLog(AbstractBaseProcessor.class);
	private static final Map EXPRESSION_CACHE = new LRUMap(1000);
	
	protected CandidatePromotionItems couldOfferApplyToOrderItems(Offer offer, List<PromotableOrderItem> promotableOrderItems) {
    	CandidatePromotionItems candidates = new CandidatePromotionItems();
    	if (offer.getQualifyingItemCriteria() == null || offer.getQualifyingItemCriteria().size() == 0) {
    		candidates.setMatchedQualifier(true);
    	} else {
    		for (OfferItemCriteria criteria : offer.getQualifyingItemCriteria()) {
    			checkForItemRequirements(candidates, criteria, promotableOrderItems, true);
    			if (!candidates.isMatchedQualifier()) {
    				break;
    			}
    		}
    	}
    	
    	if (offer.getType().equals(OfferType.ORDER_ITEM) && offer.getTargetItemCriteria() != null) {
	    	checkForItemRequirements(candidates, offer.getTargetItemCriteria(), promotableOrderItems, false);
    	}
    	
    	return candidates;
    }
	
	protected void checkForItemRequirements(CandidatePromotionItems candidates, OfferItemCriteria criteria, List<PromotableOrderItem> promotableOrderItems, boolean isQualifier) {
		boolean matchFound = false;
		int criteriaQuantity = criteria.getQuantity();
		
		if (criteriaQuantity > 0) {			
			// If matches are found, add the candidate items to a list and store it with the itemCriteria 
			// for this promotion.
			for (PromotableOrderItem item : promotableOrderItems) {
				if (couldOrderItemMeetOfferRequirement(criteria, item)) {
					if (isQualifier) {
						candidates.addQualifier(criteria, item);
					} else {
						candidates.addTarget(item);
					}
					matchFound = true;
				}
			}
		}
		
		if (isQualifier) {
			candidates.setMatchedQualifier(matchFound);
		} else {
			candidates.setMatchedTarget(matchFound);
		}
	}
	
	protected boolean couldOrderItemMeetOfferRequirement(OfferItemCriteria criteria, PromotableOrderItem discreteOrderItem) {
        boolean appliesToItem = false;

        if (criteria.getOrderItemMatchRule() != null && criteria.getOrderItemMatchRule().trim().length() != 0) {
            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("discreteOrderItem", discreteOrderItem.getDelegate());
            Boolean expressionOutcome = executeExpression(criteria.getOrderItemMatchRule(), vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToItem = true;
            }
        } else {
            appliesToItem = true;
        }

        return appliesToItem;
    }
	
	/**
     * Private method used by couldOfferApplyToOrder to execute the MVEL expression in the
     * appliesToOrderRules to determine if this offer can be applied.
     *
     * @param expression
     * @param vars
     * @return a Boolean object containing the result of executing the MVEL expression
     */
    public Boolean executeExpression(String expression, Map<String, Object> vars) {
        try {
            Serializable exp;
            synchronized (EXPRESSION_CACHE) {
                exp = (Serializable) EXPRESSION_CACHE.get(expression);
                if (exp == null) {
                    ParserContext context = new ParserContext();
                    context.addImport("OfferType", OfferType.class);
                    context.addImport("FulfillmentGroupType", FulfillmentGroupType.class);
                    context.addImport("MVEL", MVEL.class);
                    //            StringBuffer completeExpression = new StringBuffer(functions.toString());
                    //            completeExpression.append(" ").append(expression);
                    exp = MVEL.compileExpression(expression, context);

                    EXPRESSION_CACHE.put(expression, exp);
                }
            }

			Object test = MVEL.executeExpression(exp, vars);
			
			return (Boolean) test;
		} catch (Exception e) {
			//Unable to execute the MVEL expression for some reason
			//Return false, but notify about the bad expression through logs
			LOG.info("Unable to parse and/or execute an mvel expression. Reporting to the logs and returning false for the match expression", e);
			return false;
		}

    }
    
    /**
	 * We were not able to meet all of the ItemCriteria for a promotion, but some of the items were
	 * marked as qualifiers or targets.  This method removes those items from being used as targets or
	 * qualifiers so they are eligible for other promotions.
	 * @param chargeableItems
	 */
	protected void clearAllNonFinalizedQuantities(List<PromotableOrderItem> chargeableItems) {
		for(PromotableOrderItem chargeableItem : chargeableItems) {
			chargeableItem.clearAllNonFinalizedQuantities();
		}
	}
	
	protected void finalizeQuantities(List<PromotableOrderItem> chargeableItems) {
		for(PromotableOrderItem chargeableItem : chargeableItems) {
			chargeableItem.finalizeQuantities();
		}
	}
	
	public void clearOffersandAdjustments(PromotableOrder order) {
        order.removeAllCandidateOffers();
        order.removeAllAdjustments();
    }
	
	public List<Offer> filterOffers(List<Offer> offers, Customer customer) {
        List<Offer> filteredOffers = new ArrayList<Offer>();
        if (offers != null && !offers.isEmpty()) {
            filteredOffers = removeOutOfDateOffers(offers);
            filteredOffers = removeInvalidCustomerOffers(filteredOffers, customer);
        }
        return filteredOffers;
    }

	/**
     * Removes all out of date offers.  If an offer does not have a start date, or the start
     * date is a later date, that offer will be removed.  Offers without a start date should
     * not be processed.  If the offer has a end date that has already passed, that offer
     * will be removed.  Offers without a end date will be processed if the start date
     * is prior to the transaction date.
     *
     * @param offers
     * @return List of Offers with valid dates
     */
    protected List<Offer> removeOutOfDateOffers(List<Offer> offers){
        Date now = SystemTime.asDate();
        List<Offer> offersToRemove = new ArrayList<Offer>();
        for (Offer offer : offers) {
            if ((offer.getStartDate() == null) || (offer.getStartDate().after(now))){
                offersToRemove.add(offer);
            } else if (offer.getEndDate() != null && offer.getEndDate().before(now)){
                offersToRemove.add(offer);
            }
        }
        // remove all offers in the offersToRemove list from original offers list
        for (Offer offer : offersToRemove) {
            offers.remove(offer);
        }
        return offers;
    }

    /**
     * Private method that takes in a list of Offers and removes all Offers from the list that
     * does not apply to this customer.
     *
     * @param offers
     * @param customer
     * @return List of Offers that apply to this customer
     */
    protected List<Offer> removeInvalidCustomerOffers(List<Offer> offers, Customer customer){
        List<Offer> offersToRemove = new ArrayList<Offer>();
        for (Offer offer : offers) {
            if (!couldOfferApplyToCustomer(offer, customer)) {
                offersToRemove.add(offer);
            }
        }
        // remove all offers in the offersToRemove list from original offers list
        for (Offer offer : offersToRemove) {
            offers.remove(offer);
        }
        return offers;
    }
    
    /**
     * Private method which executes the appliesToCustomerRules in the Offer to determine if this Offer
     * can be applied to the Customer.
     *
     * @param offer
     * @param customer
     * @return true if offer can be applied, otherwise false
     */
    protected boolean couldOfferApplyToCustomer(Offer offer, Customer customer) {
        boolean appliesToCustomer = false;
        
        String rule = null;
        if (!StringUtils.isEmpty(offer.getAppliesToCustomerRules())) {
        	rule = offer.getAppliesToCustomerRules();
        } else {
        	OfferRule customerRule = offer.getOfferMatchRules().get(OfferRuleType.CUSTOMER.getType());
        	if (customerRule != null) {
        		rule = customerRule.getMatchRule();
        	}
        }

        if (rule != null) {
            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("customer", customer);
            Boolean expressionOutcome = executeExpression(rule, vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToCustomer = true;
            }
        } else {
            appliesToCustomer = true;
        }

        return appliesToCustomer;
    }
}
