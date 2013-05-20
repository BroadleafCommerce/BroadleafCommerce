/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
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
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.TimeDTO;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.rule.MvelHelper;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.hibernate.tool.hbm2x.StringUtils;
import org.joda.time.LocalDateTime;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Resource;

/**
 * 
 * @author jfischer
 *
 */
public abstract class AbstractBaseProcessor implements BaseProcessor {

    private static final Log LOG = LogFactory.getLog(AbstractBaseProcessor.class);
    private static final Map EXPRESSION_CACHE = new LRUMap(1000);
    
    @Resource(name = "blOfferTimeZoneProcessor")
    protected OfferTimeZoneProcessor offerTimeZoneProcessor;
    
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
            for (OfferItemCriteria criteria : offer.getTargetItemCriteria()) {
                checkForItemRequirements(candidates, criteria, promotableOrderItems, false);
                if (!candidates.isMatchedTarget()) {
                    break;
                }
            }
        }
        
        if (candidates.isMatchedQualifier()) {
            if (! meetsItemQualifierSubtotal(offer, candidates)) {
                candidates.setMatchedQualifier(false);
            }
        }       
        
        return candidates;
    }
    
    private boolean isEmpty(Collection<? extends Object> collection) {
        return (collection == null || collection.size() == 0);
    }

    private boolean hasPositiveValue(Money money) {
        return (money != null && money.greaterThan(Money.ZERO));
    }
    
    protected boolean meetsItemQualifierSubtotal(Offer offer, CandidatePromotionItems candidateItem) {
        Money qualifyingSubtotal = offer.getQualifyingItemSubTotal(); 
        if (! hasPositiveValue(qualifyingSubtotal)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Offer " + offer.getName() + " does not have an item subtotal requirement.");
            }
            return true;
        }

        if (isEmpty(offer.getQualifyingItemCriteria())) {
            if (OfferType.ORDER_ITEM.equals(offer.getType())) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Offer " + offer.getName() + " has a subtotal item requirement but no item qualification criteria.");
                }
                return false;
            } else {            
                // Checking if targets meet subtotal for item offer with no item criteria.
                Money accumulatedTotal = null;
                for (PromotableOrderItem orderItem : candidateItem.getCandidateTargets()) {                     
                    Money itemPrice = orderItem.getCurrentBasePrice().multiply(orderItem.getQuantity());
                    accumulatedTotal = accumulatedTotal==null?itemPrice:accumulatedTotal.add(itemPrice);
                    if (accumulatedTotal.greaterThan(qualifyingSubtotal)) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("Offer " + offer.getName() + " meets qualifying item subtotal.");
                        }
                        return true;
                    }
                }
            }
            
            if (LOG.isDebugEnabled()) {
                LOG.debug("Offer " + offer.getName() + " does not meet qualifying item subtotal.");
            }
        } else {
            if (candidateItem.getCandidateQualifiersMap() != null) {
                Money accumulatedTotal = null;
                Set<PromotableOrderItem> usedItems = new HashSet<PromotableOrderItem>();
                for (OfferItemCriteria criteria : candidateItem.getCandidateQualifiersMap().keySet()) {
                    List<PromotableOrderItem> promotableItems = candidateItem.getCandidateQualifiersMap().get(criteria);
                    if (promotableItems != null) {
                        for (PromotableOrderItem item : promotableItems) {
                            if (!usedItems.contains(item)) {
                                usedItems.add(item);
                                Money itemPrice = item.getCurrentBasePrice().multiply(item.getQuantity());
                                accumulatedTotal = accumulatedTotal==null?itemPrice:accumulatedTotal.add(itemPrice);
                                if (accumulatedTotal.greaterThan(qualifyingSubtotal)) {
                                    if (LOG.isTraceEnabled()) {
                                        LOG.trace("Offer " + offer.getName() + " meets the item subtotal requirement.");
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Offer " + offer.getName() + " does not meet the item subtotal qualifications.");
        }
        return false;

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
    
    protected boolean couldOrderItemMeetOfferRequirement(OfferItemCriteria criteria, PromotableOrderItem orderItem) {
        boolean appliesToItem = false;

        if (criteria.getMatchRule() != null && criteria.getMatchRule().trim().length() != 0) {
            HashMap<String, Object> vars = new HashMap<String, Object>();
            orderItem.updateRuleVariables(vars);
            Boolean expressionOutcome = executeExpression(criteria.getMatchRule(), vars);
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
                    context.addImport("FulfillmentType", FulfillmentType.class);
                    context.addImport("MVEL", MVEL.class);
                    context.addImport("MvelHelper", MvelHelper.class);
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
            LOG.warn("Unable to parse and/or execute an mvel expression. Reporting to the logs and returning false " +
                    "for the match expression:" + expression, e);
            return false;
        }

    }
    
    /**
     * We were not able to meet all of the ItemCriteria for a promotion, but some of the items were
     * marked as qualifiers or targets.  This method removes those items from being used as targets or
     * qualifiers so they are eligible for other promotions.
     * @param priceDetails
     */
    protected void clearAllNonFinalizedQuantities(List<PromotableOrderItemPriceDetail> priceDetails) {
        for (PromotableOrderItemPriceDetail priceDetail : priceDetails) {
            priceDetail.clearAllNonFinalizedQuantities();
        }
    }
    
    /**
     * Updates the finalQuanties for the PromotionDiscounts and PromotionQualifiers. 
     * Called after we have confirmed enough qualifiers and targets for the promotion.
     * @param priceDetails
     */
    protected void finalizeQuantities(List<PromotableOrderItemPriceDetail> priceDetails) {
        for (PromotableOrderItemPriceDetail priceDetail : priceDetails) {
            priceDetail.finalizeQuantities();
        }
    }
    
    /**
     * Checks to see if the discountQty matches the detailQty.   If not, splits the 
     * priceDetail.
     * 
     * @param priceDetails
     */
    protected void splitDetailsIfNecessary(List<PromotableOrderItemPriceDetail> priceDetails) {
        for (PromotableOrderItemPriceDetail priceDetail : priceDetails) {
            PromotableOrderItemPriceDetail splitDetail = priceDetail.splitIfNecessary();
            if (splitDetail != null) {
                priceDetail.getPromotableOrderItem().getPromotableOrderItemPriceDetails().add(splitDetail);
            }
        }
    }

    @Override
    public List<Offer> filterOffers(List<Offer> offers, Customer customer) {
        List<Offer> filteredOffers = new ArrayList<Offer>();
        if (offers != null && !offers.isEmpty()) {
            filteredOffers = removeOutOfDateOffers(offers);
            offers = removeTimePeriodOffers(offers);
            offers = removeInvalidRequestOffers(offers);
            filteredOffers = removeInvalidCustomerOffers(filteredOffers, customer);
        }
        return filteredOffers;
    }

    protected List<Offer> removeInvalidRequestOffers(List<Offer> offers) {
        RequestDTO requestDTO = null;
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            BroadleafRequestContext.getBroadleafRequestContext().getRequestDTO();
        }

        List<Offer> offersToRemove = new ArrayList<Offer>();
        for (Offer offer : offers) {
            if (!couldOfferApplyToRequestDTO(offer, requestDTO)) {
                offersToRemove.add(offer);
            }
        }
        // remove all offers in the offersToRemove list from original offers list
        for (Offer offer : offersToRemove) {
            offers.remove(offer);
        }
        return offers;

    }

    protected boolean couldOfferApplyToRequestDTO(Offer offer, RequestDTO requestDTO) {
        boolean appliesToRequestRule = false;

        String rule = null;

        OfferRule customerRule = offer.getOfferMatchRules().get(OfferRuleType.REQUEST.getType());
        if (customerRule != null) {
            rule = customerRule.getMatchRule();
        }

        if (rule != null) {
            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("request", requestDTO);
            Boolean expressionOutcome = executeExpression(rule, vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToRequestRule = true;
            }
        } else {
            appliesToRequestRule = true;
        }

        return appliesToRequestRule;
    }
    /**
     * Removes all offers that are not within the timezone and timeperiod of the offer.  
     * If an offer does not fall within the timezone or timeperiod rule,
     * that offer will be removed.  
     *
     * @param offers
     * @return List of Offers within the timezone or timeperiod of the offer
     */
    protected List<Offer> removeTimePeriodOffers(List<Offer> offers) {
        List<Offer> offersToRemove = new ArrayList<Offer>();

        for (Offer offer : offers) {
            if (!couldOfferApplyToTimePeriod(offer)) {
                offersToRemove.add(offer);
            }
        }
        // remove all offers in the offersToRemove list from original offers list
        for (Offer offer : offersToRemove) {
            offers.remove(offer);
        }
        return offers;
    }

    protected boolean couldOfferApplyToTimePeriod(Offer offer) {
        boolean appliesToTimePeriod = false;

        String rule = null;

        OfferRule timeRule = offer.getOfferMatchRules().get(OfferRuleType.TIME.getType());
        if (timeRule != null) {
            rule = timeRule.getMatchRule();
        }
        
        if (rule != null) {
            TimeZone timeZone = getOfferTimeZoneProcessor().getTimeZone(offer);
            TimeDTO timeDto = new TimeDTO(SystemTime.asCalendar(timeZone));
            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("time", timeDto);
            Boolean expressionOutcome = executeExpression(rule, vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToTimePeriod = true;
            }
        } else {
            appliesToTimePeriod = true;
        }

        return appliesToTimePeriod;
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
        List<Offer> offersToRemove = new ArrayList<Offer>();
        for (Offer offer : offers) {
            TimeZone timeZone = getOfferTimeZoneProcessor().getTimeZone(offer);

            Calendar current = new GregorianCalendar(timeZone);
            Calendar start = null;
            if (offer.getStartDate() != null) {
                LocalDateTime startDate = new LocalDateTime(offer.getStartDate());
                start = new GregorianCalendar(timeZone);
                start.set(Calendar.YEAR, startDate.getYear());
                start.set(Calendar.MONTH, startDate.getMonthOfYear() - 1);
                start.set(Calendar.DAY_OF_MONTH, startDate.getDayOfMonth());
                start.set(Calendar.HOUR_OF_DAY, startDate.getHourOfDay());
                start.set(Calendar.MINUTE, startDate.getMinuteOfHour());
                start.set(Calendar.SECOND, startDate.getSecondOfMinute());
                start.get(Calendar.HOUR_OF_DAY);//do not delete this line
                start.get(Calendar.MINUTE);
                if (LOG.isTraceEnabled()) {
                    LOG.debug("Offer: " + offer.getName() + " timeZone:" + timeZone + " startTime:" + start.getTime() + " currentTime:" + current.getTime());
                }
            }
            Calendar end = null;
            if (offer.getEndDate() != null) {
                LocalDateTime endDate = new LocalDateTime(offer.getEndDate());
                end = new GregorianCalendar(timeZone);
                end.set(Calendar.YEAR, endDate.getYear());
                end.set(Calendar.MONTH, endDate.getMonthOfYear() - 1);
                end.set(Calendar.DAY_OF_MONTH, endDate.getDayOfMonth());
                end.set(Calendar.HOUR_OF_DAY, endDate.getHourOfDay());
                end.set(Calendar.MINUTE, endDate.getMinuteOfHour());
                end.set(Calendar.SECOND, endDate.getSecondOfMinute());
                end.get(Calendar.HOUR_OF_DAY);//do not delete this line
                end.get(Calendar.MINUTE);
                if (LOG.isTraceEnabled()) {
                    LOG.debug("Offer: " + offer.getName() + " endTime:" + start.getTime());
                }
            }
            if ((offer.getStartDate() == null) || (start.after(current))) {
                offersToRemove.add(offer);
            } else if (offer.getEndDate() != null && end.before(current)) {
                offersToRemove.add(offer);
            }
        }
        // remove all offers in the offersToRemove list from original offers list
        for (Offer offer : offersToRemove) {
            offers.remove(offer);
        }
        return offers;

        // return offers;
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

    public OfferTimeZoneProcessor getOfferTimeZoneProcessor() {
        return offerTimeZoneProcessor;
    }

    public void setOfferTimeZoneProcessor(OfferTimeZoneProcessor offerTimeZoneProcessor) {
        this.offerTimeZoneProcessor = offerTimeZoneProcessor;
    }

}
