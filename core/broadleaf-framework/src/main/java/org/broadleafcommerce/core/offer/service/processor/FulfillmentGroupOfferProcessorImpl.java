/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.BankersRounding;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferOfferRuleXref;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.FulfillmentGroupOfferPotential;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableFulfillmentGroup;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableFulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.type.OfferRuleType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 * 
 */
@Service("blFulfillmentGroupOfferProcessor")
public class FulfillmentGroupOfferProcessorImpl extends OrderOfferProcessorImpl implements FulfillmentGroupOfferProcessor {

    @Override
    public void filterFulfillmentGroupLevelOffer(PromotableOrder order, List<PromotableCandidateFulfillmentGroupOffer> qualifiedFGOffers, Offer offer) {
        for (PromotableFulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            boolean fgLevelQualification = false;
            fgQualification: {
                // handle legacy fields in addition to the 1.5 order rule field
                if (couldOfferApplyToOrder(offer, order, fulfillmentGroup)) {
                    fgLevelQualification = true;
                    break fgQualification;
                }
                for (PromotableOrderItem discreteOrderItem : order.getAllOrderItems()) {
                    if (couldOfferApplyToOrder(offer, order, discreteOrderItem, fulfillmentGroup)) {
                        fgLevelQualification = true;
                        break fgQualification;
                    }
                }
            }
            if (fgLevelQualification) {
                fgLevelQualification = false;
                // handle 1.5 FG field
                if (couldOfferApplyToFulfillmentGroup(offer, fulfillmentGroup)) {
                    fgLevelQualification = true;
                }
            }

            // Item Qualification - new for 1.5!
            if (fgLevelQualification) {
                
                CandidatePromotionItems candidates = couldOfferApplyToOrderItems(offer, fulfillmentGroup.getDiscountableOrderItems());
                // couldn't qualify based on the items within this fulfillment group, jump out and now try to validate based
                // on all the items in the order across all fulfillment groups (not the default behavior)
                if (!candidates.isMatchedQualifier() && getQualifyGroupAcrossAllOrderItems(fulfillmentGroup)) {
                    candidates = couldOfferApplyToOrderItems(offer, order.getAllOrderItems());
                }
                
                if (candidates.isMatchedQualifier()) {
                    PromotableCandidateFulfillmentGroupOffer candidateOffer = createCandidateFulfillmentGroupOffer(offer, qualifiedFGOffers, fulfillmentGroup);
                    candidateOffer.getCandidateQualifiersMap().putAll(candidates.getCandidateQualifiersMap());
                }
            }
        }
    }
    
    /**
     * Whether or not items across the entire order should be considered in item-level qualifiers for the given fulfillment 
     * group. Default behavior is to use only the items within the fulfillment group for the item-level qualifiers.
     * 
     * @param fg the fulfillment group that we are attempting to apply item-level qualifiers to
     * @return
     */
    protected boolean getQualifyGroupAcrossAllOrderItems(PromotableFulfillmentGroup fg) {
        return BLCSystemProperty.resolveBooleanSystemProperty("promotion.fulfillmentgroup.qualifyAcrossAllOrderItems", false);
    }

    @Override
    public void calculateFulfillmentGroupTotal(PromotableOrder order) {
        Money totalFulfillmentCharges = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, order.getOrderCurrency());
        for (PromotableFulfillmentGroup fulfillmentGroupMember : order.getFulfillmentGroups()) {
            FulfillmentGroup fulfillmentGroup = fulfillmentGroupMember.getFulfillmentGroup();
            Money fulfillmentCharges;
            if (fulfillmentGroup.getShippingOverride()) {
                fulfillmentCharges = fulfillmentGroup.getFulfillmentPrice();
            } else {
                fulfillmentCharges = fulfillmentGroupMember.getFinalizedPriceWithAdjustments();
                fulfillmentGroupMember.getFulfillmentGroup().setFulfillmentPrice(fulfillmentCharges);
            }
            totalFulfillmentCharges = totalFulfillmentCharges.add(fulfillmentCharges);
        }
        order.setTotalFufillmentCharges(totalFulfillmentCharges);
    }

    protected boolean couldOfferApplyToFulfillmentGroup(Offer offer, PromotableFulfillmentGroup fulfillmentGroup) {
        boolean appliesToItem = false;

        OfferRule rule = null;
        OfferOfferRuleXref ruleXref = offer.getOfferMatchRulesXref().get(OfferRuleType.FULFILLMENT_GROUP.getType());
        if (ruleXref != null && ruleXref.getOfferRule() != null) {
            rule = ruleXref.getOfferRule();
        }

        if (rule != null && rule.getMatchRule() != null) {
            HashMap<String, Object> vars = new HashMap<String, Object>();
            fulfillmentGroup.updateRuleVariables(vars);
            Boolean expressionOutcome = executeExpression(rule.getMatchRule(), vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToItem = true;
            }
        } else {
            appliesToItem = true;
        }

        return appliesToItem;
    }

    protected PromotableCandidateFulfillmentGroupOffer createCandidateFulfillmentGroupOffer(Offer offer, List<PromotableCandidateFulfillmentGroupOffer> qualifiedFGOffers, PromotableFulfillmentGroup fulfillmentGroup) {
        PromotableCandidateFulfillmentGroupOffer promotableCandidateFulfillmentGroupOffer =
                promotableItemFactory.createPromotableCandidateFulfillmentGroupOffer(fulfillmentGroup, offer);
        qualifiedFGOffers.add(promotableCandidateFulfillmentGroupOffer);

        return promotableCandidateFulfillmentGroupOffer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean applyAllFulfillmentGroupOffers(List<PromotableCandidateFulfillmentGroupOffer> qualifiedFGOffers, PromotableOrder order) {
        Map<FulfillmentGroupOfferPotential, List<PromotableCandidateFulfillmentGroupOffer>> offerMap = new HashMap<FulfillmentGroupOfferPotential, List<PromotableCandidateFulfillmentGroupOffer>>();
        for (PromotableCandidateFulfillmentGroupOffer candidate : qualifiedFGOffers) {
            FulfillmentGroupOfferPotential potential = new FulfillmentGroupOfferPotential();
            potential.setOffer(candidate.getOffer());
            if (offerMap.get(potential) == null) {
                offerMap.put(potential, new ArrayList<PromotableCandidateFulfillmentGroupOffer>());
            }
            offerMap.get(potential).add(candidate);
        }
        List<FulfillmentGroupOfferPotential> potentials = new ArrayList<FulfillmentGroupOfferPotential>();
        for (FulfillmentGroupOfferPotential potential : offerMap.keySet()) {
            List<PromotableCandidateFulfillmentGroupOffer> fgOffers = offerMap.get(potential);
            Collections.sort(fgOffers, new ReverseComparator(new BeanComparator("discountedAmount", new NullComparator())));
            Collections.sort(fgOffers, new BeanComparator("priority", new NullComparator()));

            if (potential.getOffer().isLimitedUsePerOrder() && fgOffers.size() > potential.getOffer().getMaxUsesPerOrder()) {
                for (int j = potential.getOffer().getMaxUsesPerOrder(); j < fgOffers.size(); j++) {
                    fgOffers.remove(j);
                }
            }

            filterOffersByQualifyingAndSubtotalRequirements(order, fgOffers);
            
            for (PromotableCandidateFulfillmentGroupOffer candidate : fgOffers) {
                if (potential.getTotalSavings().getAmount().equals(BankersRounding.zeroAmount())) {
                    BroadleafCurrency currency = order.getOrderCurrency();
                    if (currency != null) {
                        potential.setTotalSavings(new Money(BigDecimal.ZERO, currency.getCurrencyCode()));
                    } else {
                        potential.setTotalSavings(new Money(BigDecimal.ZERO));
                    }
                }

                Money priceBeforeAdjustments = candidate.getFulfillmentGroup().calculatePriceWithoutAdjustments();
                Money discountedPrice = candidate.getDiscountedPrice();
                potential.setTotalSavings(potential.getTotalSavings().add(priceBeforeAdjustments.subtract(discountedPrice)));
                potential.setPriority(candidate.getOffer().getPriority());
            }

            potentials.add(potential);
        }

        // Sort fg potentials by priority and discount
        Collections.sort(potentials, new BeanComparator("totalSavings", Collections.reverseOrder()));
        Collections.sort(potentials, new BeanComparator("priority"));
        potentials = removeTrailingNotCombinableFulfillmentGroupOffers(potentials);

        boolean fgOfferApplied = false;
        for (FulfillmentGroupOfferPotential potential : potentials) {
            Offer offer = potential.getOffer();

            boolean alreadyContainsTotalitarianOffer = order.isTotalitarianOfferApplied();
            List<PromotableCandidateFulfillmentGroupOffer> candidates = offerMap.get(potential);
            for (PromotableCandidateFulfillmentGroupOffer candidate : candidates) {
                applyFulfillmentGroupOffer(candidate.getFulfillmentGroup(), candidate);
                fgOfferApplied = true;
            }
            for (PromotableFulfillmentGroup fg : order.getFulfillmentGroups()) {
                fg.chooseSaleOrRetailAdjustments();
            }

            if ((offer.isTotalitarianOffer() != null && offer.isTotalitarianOffer()) || alreadyContainsTotalitarianOffer) {
                fgOfferApplied = compareAndAdjustFulfillmentGroupOffers(order, fgOfferApplied);
                if (fgOfferApplied) {
                    break;
                }
            } else if (!offer.isCombinableWithOtherOffers()) {
                break;
            }
        }

        return fgOfferApplied;
    }

    protected void filterOffersByQualifyingAndSubtotalRequirements(PromotableOrder order, List<PromotableCandidateFulfillmentGroupOffer> fgOffers) {
        Iterator<PromotableCandidateFulfillmentGroupOffer> fgOffersIterator = fgOffers.iterator();

        while (fgOffersIterator.hasNext()) {
            PromotableCandidateFulfillmentGroupOffer offer = fgOffersIterator.next();

            if (!orderMeetsQualifyingSubtotalRequirements(order, offer) || !orderMeetsSubtotalRequirements(order, offer)) {
                fgOffersIterator.remove();
            }
        }
    }

    protected boolean orderMeetsQualifyingSubtotalRequirements(PromotableOrder order, PromotableCandidateFulfillmentGroupOffer fgOffer) {
        return offerServiceUtilities.orderMeetsQualifyingSubtotalRequirements(order, fgOffer.getOffer(), fgOffer.getCandidateQualifiersMap());
    }

    protected boolean orderMeetsSubtotalRequirements(PromotableOrder order, PromotableCandidateFulfillmentGroupOffer fgOffer) {
        return offerServiceUtilities.orderMeetsSubtotalRequirements(order, fgOffer.getOffer());
    }

    protected boolean compareAndAdjustFulfillmentGroupOffers(PromotableOrder order, boolean fgOfferApplied) {
        Money regularOrderDiscountShippingTotal =
                BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, order.getOrderCurrency());
        regularOrderDiscountShippingTotal =
                regularOrderDiscountShippingTotal.add(order.calculateSubtotalWithoutAdjustments());
        for (PromotableFulfillmentGroup fg : order.getFulfillmentGroups()) {
            regularOrderDiscountShippingTotal =
                    regularOrderDiscountShippingTotal.add(fg.getFinalizedPriceWithAdjustments());
        }

        Money discountOrderRegularShippingTotal = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, order.getOrderCurrency());
        discountOrderRegularShippingTotal = discountOrderRegularShippingTotal.add(order.calculateSubtotalWithAdjustments());
        for (PromotableFulfillmentGroup fg : order.getFulfillmentGroups()) {
            discountOrderRegularShippingTotal = discountOrderRegularShippingTotal.add(fg.calculatePriceWithoutAdjustments());
        }

        if (discountOrderRegularShippingTotal.lessThan(regularOrderDiscountShippingTotal)) {
            // order/item offer is better; remove totalitarian fulfillment group offer and process other fg offers
            order.removeAllCandidateFulfillmentOfferAdjustments();
            fgOfferApplied = false;
        } else {
            // totalitarian fg offer is better; remove all order/item offers
            order.removeAllCandidateOrderOfferAdjustments();
            order.removeAllCandidateItemOfferAdjustments();
            order.getOrder().setSubTotal(order.calculateSubtotalWithAdjustments());
        }
        return fgOfferApplied;
    }

    protected void applyFulfillmentGroupOffer(PromotableFulfillmentGroup promotableFulfillmentGroup, PromotableCandidateFulfillmentGroupOffer fulfillmentGroupOffer) {
        if (promotableFulfillmentGroup.canApplyOffer(fulfillmentGroupOffer)) {
            PromotableFulfillmentGroupAdjustment promotableFulfillmentGroupAdjustment = promotableItemFactory.createPromotableFulfillmentGroupAdjustment(fulfillmentGroupOffer, promotableFulfillmentGroup);
            promotableFulfillmentGroup.addCandidateFulfillmentGroupAdjustment(promotableFulfillmentGroupAdjustment);
        }
    }

    @Override
    public List<FulfillmentGroupOfferPotential> removeTrailingNotCombinableFulfillmentGroupOffers(List<FulfillmentGroupOfferPotential> candidateOffers) {
        List<FulfillmentGroupOfferPotential> remainingCandidateOffers = new ArrayList<FulfillmentGroupOfferPotential>();
        int offerCount = 0;
        for (FulfillmentGroupOfferPotential candidateOffer : candidateOffers) {
            if (offerCount == 0) {
                remainingCandidateOffers.add(candidateOffer);
            } else {
                if (candidateOffer.getOffer().isCombinableWithOtherOffers() &&
                        !candidateOffer.getOffer().isTotalitarianOffer()) {
                    remainingCandidateOffers.add(candidateOffer);
                }
            }
            offerCount++;
        }
        return remainingCandidateOffers;
    }
}
