package org.broadleafcommerce.core.offer.service.processor;

import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.CandidateQualifiedOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.candidate.CandidatePromotionItems;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.springframework.stereotype.Service;

@Service("blFulfillmentGroupOfferProcessor")
public class FulfillmentGroupOfferProcessorImpl extends OrderOfferProcessorImpl implements FulfillmentGroupOfferProcessor {

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.service.processor.FulfillmentGroupOfferProcessor#filterFulfillmentGroupLevelOffer(org.broadleafcommerce.core.order.domain.Order, java.util.List, java.util.List, org.broadleafcommerce.core.offer.domain.Offer)
	 */
	public void filterFulfillmentGroupLevelOffer(Order order, List<CandidateFulfillmentGroupOffer> qualifiedFGOffers, List<OrderItem> discreteOrderItems, Offer offer) {
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
	            //handle 1.5 FG field
	            if(couldOfferApplyToFulfillmentGroup(offer, fulfillmentGroup)) {
	            	fgLevelQualification = true;
	                break fgQualification;
	            }
	    	}
			//Item Qualification - new for 1.5!
			if (fgLevelQualification) {
				CandidatePromotionItems candidates = couldOfferApplyToOrderItems(offer, discreteOrderItems);
				if (candidates.isMatchedCandidate()) {
					CandidateQualifiedOffer candidateOffer = createCandidateFulfillmentGroupOffer(offer, qualifiedFGOffers, fulfillmentGroup);
					candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateItemsMap());
				}
			}
		}
	}
	
	protected boolean couldOfferApplyToFulfillmentGroup(Offer offer, FulfillmentGroup fulfillmentGroup) {
        boolean appliesToItem = false;

        if (offer.getAppliesToFulfillmentGroupRules() != null && offer.getAppliesToFulfillmentGroupRules().trim().length() != 0) {
            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("fulfillmentGroup", fulfillmentGroup);
            Boolean expressionOutcome = executeExpression(offer.getAppliesToFulfillmentGroupRules(), vars);
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
}
