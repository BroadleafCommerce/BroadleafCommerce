package org.broadleafcommerce.core.offer.service.processor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.candidate.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupType;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

public class BaseProcessor {

	private static final LRUMap EXPRESSION_CACHE = new LRUMap(1000);
	
	protected CandidatePromotionItems couldOfferApplyToOrderItems(Offer offer, List<OrderItem> discreteOrderItems) {
    	CandidatePromotionItems candidates = new CandidatePromotionItems();
    	if (offer.getQualifyingItemCriteria() == null && offer.getQualifyingItemCriteria().size() == 0) {
    		candidates.setMatchedCandidate(false);
    	} else {
    		for (OfferItemCriteria criteria : offer.getQualifyingItemCriteria()) {
    			checkForItemRequirements(candidates, criteria, discreteOrderItems);
    			if (!candidates.isMatchedCandidate()) {
    				break;
    			}
    		}
    	}
    	return candidates;
    }
	
	protected void checkForItemRequirements(CandidatePromotionItems candidates, OfferItemCriteria criteria, List<OrderItem> discreteOrderItems) {
		int matchCount = 0;
		
		Iterator<OrderItem> itr = discreteOrderItems.iterator();
		if (criteria.getRequiresQuantity() > 0) {			
			// If matches are found, add the candidate items to a list and store it with the itemCriteria 
			// for this promotion.
			while (matchCount < criteria.getRequiresQuantity() && itr.hasNext()) {
				DiscreteOrderItem item = (DiscreteOrderItem) itr.next();
				if (couldOrderItemQualifyForOffer(criteria, item)) {
					candidates.addCandidateItem(criteria, item);
					matchCount = matchCount + (item.getQuantity());
				}
			}
		}
		
		boolean matchFound = (matchCount >= criteria.getRequiresQuantity());
		candidates.setMatchedCandidate(matchFound);
	}
	
	protected boolean couldOrderItemQualifyForOffer(OfferItemCriteria criteria, DiscreteOrderItem discreteOrderItem) {
        boolean appliesToItem = false;

        if (criteria.getOrderItemMatchRule() != null && criteria.getOrderItemMatchRule().trim().length() != 0) {
            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("discreteOrderItem", discreteOrderItem);
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
    protected Boolean executeExpression(String expression, Map<String, Object> vars) {
        Serializable exp = (Serializable) EXPRESSION_CACHE.get(expression);
        if (exp == null) {
            ParserContext context = new ParserContext();
            context.addImport("OfferType", OfferType.class);
            context.addImport("FulfillmentGroupType", FulfillmentGroupType.class);
            //            StringBuffer completeExpression = new StringBuffer(functions.toString());
            //            completeExpression.append(" ").append(expression);
            exp = MVEL.compileExpression(expression.toString(), context);
        }
        EXPRESSION_CACHE.put(expression, exp);

        return (Boolean)MVEL.executeExpression(exp, vars);

    }
}
