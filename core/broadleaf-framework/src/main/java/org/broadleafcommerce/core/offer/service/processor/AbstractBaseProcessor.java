/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.offer.service.processor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.BooleanUtils;
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
import org.broadleafcommerce.core.offer.domain.OfferOfferRuleXref;
import org.broadleafcommerce.core.offer.domain.OfferPriceData;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OfferTargetCriteriaXref;
import org.broadleafcommerce.core.offer.service.OfferServiceExtensionManager;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOfferUtility;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import jakarta.annotation.Resource;

/**
 * @author jfischer
 */
public abstract class AbstractBaseProcessor implements BaseProcessor {

    private static final Log LOG = LogFactory.getLog(AbstractBaseProcessor.class);
    private static final Map EXPRESSION_CACHE = new LRUMap(1000);
    protected final PromotableOfferUtility promotableOfferUtility;
    @Resource(name = "blOfferTimeZoneProcessor")
    protected OfferTimeZoneProcessor offerTimeZoneProcessor;
    @Resource(name = "blOfferServiceExtensionManager")
    protected OfferServiceExtensionManager extensionManager;

    protected AbstractBaseProcessor(PromotableOfferUtility promotableOfferUtility) {
        this.promotableOfferUtility = promotableOfferUtility;
    }

    protected CandidatePromotionItems couldOfferApplyToOrderItems(
            Offer offer,
            List<PromotableOrderItem> promotableOrderItems
    ) {
        CandidatePromotionItems candidates = new CandidatePromotionItems();
        if (offer.getQualifyingItemCriteriaXref() == null || offer.getQualifyingItemCriteriaXref().size() == 0) {
            candidates.setMatchedQualifier(true);
        } else {
            for (OfferQualifyingCriteriaXref criteriaXref : offer.getQualifyingItemCriteriaXref()) {
                if (criteriaXref.getOfferItemCriteria() != null) {
                    checkForItemRequirements(
                            offer, candidates, criteriaXref.getOfferItemCriteria(), promotableOrderItems, true
                    );
                    if (!candidates.isMatchedQualifier()) {
                        break;
                    }
                }
            }
        }

        if (offer.getType().equals(OfferType.ORDER_ITEM) && BooleanUtils.isTrue(offer.getUseListForDiscounts())) {
            for (OfferPriceData offerPriceData : offer.getOfferPriceData()) {
                PromotableOrderItem qualifyingOrderItem = findQualifyingItemForPriceData(
                        offerPriceData, promotableOrderItems
                );
                if (qualifyingOrderItem != null) {
                    candidates.addFixedTarget(offerPriceData, qualifyingOrderItem);
                    candidates.setMatchedTarget(true);
                }
            }
        } else if (offer.getType().equals(OfferType.ORDER_ITEM) && offer.getTargetItemCriteriaXref() != null) {
            for (OfferTargetCriteriaXref xref : offer.getTargetItemCriteriaXref()) {
                checkForItemRequirements(
                        offer, candidates, xref.getOfferItemCriteria(), promotableOrderItems, false
                );
                if (!candidates.isMatchedTarget()) {
                    break;
                }
            }
        }

        if (candidates.isMatchedQualifier()) {
            if (!meetsItemQualifierSubtotal(offer, candidates)) {
                candidates.setMatchedQualifier(false);
            }
        }

        return candidates;
    }

    protected PromotableOrderItem findQualifyingItemForPriceData(
            OfferPriceData offerPriceData,
            List<PromotableOrderItem> promotableOrderItems
    ) {
        for (PromotableOrderItem promotableOrderItem : promotableOrderItems) {
            if (promotableOfferUtility.itemMatchesOfferPriceData(offerPriceData, promotableOrderItem)) {
                return promotableOrderItem;
            }
        }
        return null;
    }

    protected boolean isEmpty(Collection<? extends Object> collection) {
        return (collection == null || collection.size() == 0);
    }

    protected boolean hasPositiveValue(Money money) {
        return (money != null && money.greaterThan(Money.ZERO));
    }

    protected boolean meetsItemQualifierSubtotal(Offer offer, CandidatePromotionItems candidateItem) {
        Money qualifyingSubtotal = offer.getQualifyingItemSubTotal();
        if (!hasPositiveValue(qualifyingSubtotal)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Offer " + offer.getName() + " does not have an item subtotal requirement.");
            }
            return true;
        }

        if (isEmpty(offer.getQualifyingItemCriteriaXref())) {
            if (OfferType.ORDER_ITEM.equals(offer.getType())) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Offer " + offer.getName() + " has a subtotal item requirement but no item qualification criteria.");
                }
                return false;
            } else {
                // Checking if targets meet subtotal for item offer with no item criteria.
                Money accumulatedTotal = null;

                for (PromotableOrderItem orderItem : candidateItem.getAllCandidateTargets()) {
                    Money itemPrice = orderItem.getCurrentBasePrice().multiply(orderItem.getQuantity());
                    accumulatedTotal = accumulatedTotal == null ? itemPrice : accumulatedTotal.add(itemPrice);
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
                Set<PromotableOrderItem> usedItems = new HashSet<>();
                for (OfferItemCriteria criteria : candidateItem.getCandidateQualifiersMap().keySet()) {
                    List<PromotableOrderItem> promotableItems = candidateItem.getCandidateQualifiersMap().get(criteria);
                    if (promotableItems != null) {
                        for (PromotableOrderItem item : promotableItems) {
                            if (!usedItems.contains(item)) {
                                usedItems.add(item);
                                Money itemPrice = item.getCurrentBasePrice().multiply(item.getQuantity());
                                accumulatedTotal = accumulatedTotal == null ? itemPrice : accumulatedTotal.add(itemPrice);
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

    protected void checkForItemRequirements(
            Offer offer,
            CandidatePromotionItems candidates,
            OfferItemCriteria criteria,
            List<PromotableOrderItem> promotableOrderItems,
            boolean isQualifier
    ) {
        boolean matchFound = false;
        int criteriaQuantity = criteria.getQuantity();
        int matchedQuantity = 0;

        if (criteriaQuantity > 0) {
            // If matches are found, add the candidate items to a list and store it with the itemCriteria
            // for this promotion.
            for (PromotableOrderItem item : promotableOrderItems) {
                if (couldOrderItemMeetOfferRequirement(criteria, item)) {
                    if (isQualifier) {
                        candidates.addQualifier(criteria, item);
                    } else {
                        candidates.addTarget(criteria, item);
                        addChildOrderItemsToCandidates(offer, candidates, criteria, promotableOrderItems, item);
                    }
                    matchedQuantity += item.getQuantity();
                }
            }
            matchFound = (matchedQuantity >= criteriaQuantity);
        }

        if (isQualifier) {
            candidates.setMatchedQualifier(matchFound);
        } else {
            candidates.setMatchedTarget(matchFound);
        }
    }

    protected void addChildOrderItemsToCandidates(
            Offer offer,
            CandidatePromotionItems candidates,
            OfferItemCriteria criteria,
            List<PromotableOrderItem> promotableOrderItems,
            PromotableOrderItem item
    ) {
        if (offer.getApplyToChildItems()) {
            final List<OrderItem> childItems = item.getOrderItem().getChildOrderItems();
            if (CollectionUtils.isEmpty(childItems)) {
                return;
            }

            List<PromotableOrderItem> filteredItems = new ArrayList<>();
            filteredItems.addAll(promotableOrderItems);
            CollectionUtils.filter(filteredItems, new Predicate<PromotableOrderItem>() {
                @Override
                public boolean evaluate(PromotableOrderItem promotableOrderItem) {
                    return childItems.contains(promotableOrderItem.getOrderItem());
                }
            });

            for (PromotableOrderItem promotableOrderItem : filteredItems) {
                candidates.addTarget(criteria, promotableOrderItem);
            }
        }
    }

    protected boolean couldOrderItemMeetOfferRequirement(OfferItemCriteria criteria, PromotableOrderItem orderItem) {
        boolean appliesToItem = false;

        if (criteria.getMatchRule() != null && criteria.getMatchRule().trim().length() != 0) {
            HashMap<String, Object> vars = new HashMap<>();
            orderItem.updateRuleVariables(vars);

            if (extensionManager != null) {
                extensionManager.applyAdditionalRuleVariablesForItemOfferEvaluation(orderItem, vars);
            }

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
        Map<String, Class<?>> contextImports = new HashMap<>();

        expression = usePriceBeforeAdjustments(expression);
        contextImports.put("OfferType", OfferType.class);
        contextImports.put("FulfillmentType", FulfillmentType.class);
        return MvelHelper.evaluateRule(expression, vars, EXPRESSION_CACHE, contextImports);
    }

    protected String usePriceBeforeAdjustments(String expression) {
        return expression.replace("?price.", "?getPriceBeforeAdjustments(true).");
    }

    /**
     * We were not able to meet all of the ItemCriteria for a promotion, but some of the items were
     * marked as qualifiers or targets.  This method removes those items from being used as targets or
     * qualifiers so they are eligible for other promotions.
     *
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
     *
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
        List<Offer> filteredOffers = new ArrayList<>();
        if (offers != null && !offers.isEmpty()) {
            filteredOffers = removeOutOfDateOffers(offers);
            filteredOffers = removeTimePeriodOffers(filteredOffers);
            filteredOffers = removeInvalidRequestOffers(filteredOffers);
            filteredOffers = removeInvalidCustomerOffers(filteredOffers, customer);
        }
        return filteredOffers;
    }

    protected List<Offer> removeInvalidRequestOffers(List<Offer> offers) {
        RequestDTO requestDTO = null;
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            requestDTO = BroadleafRequestContext.getBroadleafRequestContext().getRequestDTO();
        }

        List<Offer> offersToRemove = new ArrayList<>();
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

        OfferOfferRuleXref ruleXref = offer.getOfferMatchRulesXref().get(OfferRuleType.REQUEST.getType());
        if (ruleXref != null && ruleXref.getOfferRule() != null) {
            rule = ruleXref.getOfferRule().getMatchRule();
        }

        if (rule != null) {
            HashMap<String, Object> vars = new HashMap<>();
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
        List<Offer> offersToRemove = new ArrayList<>();

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

        OfferOfferRuleXref ruleXref = offer.getOfferMatchRulesXref().get(OfferRuleType.TIME.getType());
        if (ruleXref != null && ruleXref.getOfferRule() != null) {
            rule = ruleXref.getOfferRule().getMatchRule();
        }

        if (rule != null) {
            TimeZone timeZone = getOfferTimeZoneProcessor().getTimeZone(offer);
            TimeDTO timeDto = new TimeDTO(SystemTime.asCalendar(timeZone));
            HashMap<String, Object> vars = new HashMap<>();
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
    protected List<Offer> removeOutOfDateOffers(List<Offer> offers) {
        Iterator<Offer> offersIterator = offers.iterator();

        while (offersIterator.hasNext()) {
            Offer offer = offersIterator.next();

            TimeZone offerTimeZone = getOfferTimeZoneProcessor().getTimeZone(offer);

            if (offerTimeZone == null) {
                offerTimeZone = TimeZone.getDefault();
            }

            Calendar current = SystemTime.asCalendar(offerTimeZone);

            Calendar start = null;
            Calendar end = null;

            if (offer.getStartDate() != null) {
                start = dateToCalendar(offer.getStartDate(), offerTimeZone);

                if (LOG.isTraceEnabled()) {
                    LOG.debug("Offer: " + offer.getName() + " timeZone:" + offerTimeZone + " startTime:"
                            + start.getTime() + " currentTime:" + current.getTime());
                }
            }

            if (offer.getEndDate() != null) {
                end = dateToCalendar(offer.getEndDate(), offerTimeZone);

                if (LOG.isTraceEnabled()) {
                    LOG.debug("Offer: " + offer.getName() + " endTime:" + end.getTime());
                }
            }

            if (start == null || start.after(current)) {
                offersIterator.remove();
            } else if (end != null && end.before(current)) {
                offersIterator.remove();
            }
        }

        return offers;
    }

    protected Calendar dateToCalendar(Date date, TimeZone offerTimeZone) {
        LocalDateTime offerDateTime = LocalDateTime.ofInstant(date.toInstant(), offerTimeZone.toZoneId());

        Calendar calendar = new GregorianCalendar(offerTimeZone);

        calendar.set(Calendar.YEAR, offerDateTime.getYear());
        calendar.set(Calendar.MONTH, offerDateTime.getMonthValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, offerDateTime.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, offerDateTime.getHour());
        calendar.set(Calendar.MINUTE, offerDateTime.getMinute());
        calendar.set(Calendar.SECOND, offerDateTime.getSecond());
        calendar.get(Calendar.HOUR_OF_DAY);//do not delete this line
        calendar.get(Calendar.MINUTE);

        return calendar;
    }

    /**
     * Private method that takes in a list of Offers and removes all Offers from the list that
     * does not apply to this customer.
     *
     * @param offers
     * @param customer
     * @return List of Offers that apply to this customer
     */
    protected List<Offer> removeInvalidCustomerOffers(List<Offer> offers, Customer customer) {
        List<Offer> offersToRemove = new ArrayList<>();
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
        OfferOfferRuleXref ruleXref = offer.getOfferMatchRulesXref().get(OfferRuleType.CUSTOMER.getType());
        if (ruleXref != null && ruleXref.getOfferRule() != null) {
            rule = ruleXref.getOfferRule().getMatchRule();
        }

        if (rule != null) {
            HashMap<String, Object> vars = new HashMap<>();
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
