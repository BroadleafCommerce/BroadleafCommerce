package org.broadleafcommerce.core.offer.service.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.CandidateQualifiedOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.springframework.stereotype.Service;

@Service("blOrderOfferProcessor")
public class OrderOfferProcessorImpl extends AbstractBaseProcessor implements OrderOfferProcessor {
	
	private static final Log LOG = LogFactory.getLog(OrderOfferProcessorImpl.class);

	@Resource(name="blOfferDao")
    protected OfferDao offerDao;
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.service.processor.OrderOfferProcessor#filterOrderLevelOffer(org.broadleafcommerce.core.order.domain.Order, java.util.List, java.util.List, org.broadleafcommerce.core.offer.domain.Offer)
	 */
	public void filterOrderLevelOffer(Order order, List<CandidateOrderOffer> qualifiedOrderOffers, List<DiscreteOrderItem> discreteOrderItems, Offer offer) {
		if (offer.getDiscountType().getType().equals(OfferDiscountType.FIX_PRICE.getType())) {
			LOG.warn("Offers of type ORDER may not have a discount type of FIX_PRICE. Ignoring order offer (name="+offer.getName()+")");
			return;
		}
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
			if (candidates.isMatchedQualifier()) {
				CandidateQualifiedOffer candidateOffer = createCandidateOrderOffer(order, qualifiedOrderOffers, offer);
				candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateQualifiersMap());
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
    public boolean couldOfferApplyToOrder(Offer offer, Order order) {
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
        String rule = null;
        if (offer.getAppliesToOrderRules() != null && offer.getAppliesToOrderRules().trim().length() != 0) {
        	rule = offer.getAppliesToOrderRules();
        } else {
        	OfferRule orderRule = offer.getOfferMatchRules().get(OfferRuleType.ORDER.getType());
        	if (orderRule != null) {
        		rule = orderRule.getMatchRule();
        	}
        }

        if (rule != null) {

            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("order", order);
            vars.put("offer", offer);
            if (fulfillmentGroup != null) {
                vars.put("fulfillmentGroup", fulfillmentGroup);
            }
            if (discreteOrderItem != null) {
                vars.put("discreteOrderItem", discreteOrderItem);
            }
            Boolean expressionOutcome = executeExpression(rule, vars);
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
    
    public List<CandidateOrderOffer> removeTrailingNotCombinableOrderOffers(List<CandidateOrderOffer> candidateOffers) {
        List<CandidateOrderOffer> remainingCandidateOffers = new ArrayList<CandidateOrderOffer>();
        int offerCount = 0;
        for (CandidateOrderOffer candidateOffer : candidateOffers) {
            if (offerCount == 0) {
                remainingCandidateOffers.add(candidateOffer);
            } else {
                if (candidateOffer.getOffer().isCombinableWithOtherOffers()) {
                    remainingCandidateOffers.add(candidateOffer);
                }
            }
            offerCount++;
        }
        return remainingCandidateOffers;
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
    public boolean applyAllOrderOffers(List<CandidateOrderOffer> orderOffers, Order order) {
        // If order offer is not combinable, first verify order adjustment is zero, if zero, compare item discount total vs this offer's total
        boolean orderOffersApplied = false;
        Iterator<CandidateOrderOffer> orderOfferIterator = orderOffers.iterator();
        while(orderOfferIterator.hasNext()) {
        	CandidateOrderOffer orderOffer = orderOfferIterator.next();
        	if (orderOffer.getOffer().getTreatAsNewFormat() == null || !orderOffer.getOffer().getTreatAsNewFormat()) {
        		if ((orderOffer.getOffer().isStackable()) || !order.isHasOrderAdjustments()) {
        			boolean alreadyContainsNotCombinableOfferAtAnyLevel = order.isNotCombinableOfferAppliedAtAnyLevel();
                    applyOrderOffer(orderOffer);
                    orderOffersApplied = true;
                    if (!orderOffer.getOffer().isCombinableWithOtherOffers() || alreadyContainsNotCombinableOfferAtAnyLevel) {
                    	orderOffersApplied = compareAndAdjustOrderAndItemOffers(order, orderOffersApplied);
                    	if (orderOffersApplied) {
                    		break;
                    	} else {
                    		orderOfferIterator.remove();
                    	}
                    }
                }
        	} else {
        		if (!order.containsNotStackableOrderOffer() || !order.isHasOrderAdjustments()) {
        			boolean alreadyContainsTotalitarianOffer = order.isTotalitarianOfferApplied();
        			applyOrderOffer(orderOffer);
                    orderOffersApplied = true;
                	if (
                		(orderOffer.getOffer().isTotalitarianOffer() != null && orderOffer.getOffer().isTotalitarianOffer()) ||
                		alreadyContainsTotalitarianOffer
                	) {
                		orderOffersApplied = compareAndAdjustOrderAndItemOffers(order, orderOffersApplied);
                		if (orderOffersApplied) {
                    		break;
                    	} else {
                    		orderOfferIterator.remove();
                    	}
                	} else if (!orderOffer.getOffer().isCombinableWithOtherOffers()) {
                		break;
                	}
        		}
        	}
        }
        return orderOffersApplied;
    }

	protected boolean compareAndAdjustOrderAndItemOffers(Order order, boolean orderOffersApplied) {
		if (order.getAdjustmentPrice().greaterThanOrEqual(order.calculateOrderItemsCurrentPrice())) {
			// item offer is better; remove not combinable order offer and process other order offers
			order.removeAllOrderAdjustments();
		    orderOffersApplied = false;
		} else {
			// totalitarian order offer is better; remove all item offers
			order.removeAllItemAdjustments();
			order.getSplitItems().clear();
			order.getSplitItems().addAll(order.getOrderItems());
			mergeSplitItems(order);
		}
		return orderOffersApplied;
	}
    
    
    /**
     * Private method used by applyAllOrderOffers to create an OrderAdjustment from a CandidateOrderOffer
     * and associates the OrderAdjustment to the Order.
     *
     * @param orderOffer a CandidateOrderOffer to apply to an Order
     */
    protected void applyOrderOffer(CandidateOrderOffer orderOffer) {
        OrderAdjustment orderAdjustment = offerDao.createOrderAdjustment();
        orderAdjustment.init(orderOffer.getOrder(), orderOffer.getOffer(), orderOffer.getOffer().getName());
        //add to adjustment
        orderOffer.getOrder().addOrderAdjustments(orderAdjustment);
    }
    
    protected void mergeSplitItems(Order order) {
		//If adjustments are removed - merge split items back together before adding to the cart
		Map<String, List<OrderItem>> splitMap = new HashMap<String, List<OrderItem>>();
		for (OrderItem splitItem : order.getSplitItems()) {
			if (!splitMap.containsKey(splitItem.getName())) {
				List<OrderItem> mySplits = new ArrayList<OrderItem>();
				splitMap.put(splitItem.getName(), mySplits);
			}
			splitMap.get(splitItem.getName()).add(splitItem);
		}
		Iterator<OrderItem> finalItems = order.getOrderItems().iterator();
		while(finalItems.hasNext()) {
			OrderItem nextItem = finalItems.next();
			List<OrderItem> mySplits = splitMap.get(nextItem.getName());
			if (mySplits != null && mySplits.size() > 0) {
				OrderItem cloneItem = nextItem.clone();
				cloneItem.clearAllDiscount();
				cloneItem.clearAllQualifiers();
				cloneItem.removeAllAdjustments();
				cloneItem.setQuantity(0);
				Iterator<OrderItem> splitItemIterator = mySplits.iterator();
				while(splitItemIterator.hasNext()) {
					OrderItem splitItem = splitItemIterator.next();
					if (!splitItem.isHasOrderItemAdjustments()) {
						cloneItem.setQuantity(cloneItem.getQuantity() + splitItem.getQuantity());
						splitItemIterator.remove();
					}
				}
				if (cloneItem.getQuantity() > 0) {
					mySplits.add(cloneItem);
				}
				finalItems.remove();
			}
		}
		for (String key : splitMap.keySet()) {
			List<OrderItem> mySplits = splitMap.get(key);
			if (mySplits != null && mySplits.size() > 0) {
				order.getOrderItems().addAll(mySplits);
			}
		}
	}
    
    public void calculateOrderTotal(Order order) {
		order.assignOrderItemsFinalPrice();
		order.setSubTotal(order.calculateOrderItemsFinalPrice());
	}
    
	public OfferDao getOfferDao() {
		return offerDao;
	}

	public void setOfferDao(OfferDao offerDao) {
		this.offerDao = offerDao;
	}
}
