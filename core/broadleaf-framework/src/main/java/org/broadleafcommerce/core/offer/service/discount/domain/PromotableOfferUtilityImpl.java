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
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.util.Tuple;
import org.broadleafcommerce.core.offer.domain.AdvancedOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferPriceData;
import org.broadleafcommerce.core.offer.domain.OfferTier;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferPriceDataIdentifierType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

/**
 * @author Nick Crum (ncrum)
 */
@Service("blPromotableOfferUtility")
public class PromotableOfferUtilityImpl implements PromotableOfferUtility {

    @Override
    public boolean itemMatchesOfferPriceData(OfferPriceData offerPriceData, PromotableOrderItem promotableOrderItem) {
        if (promotableOrderItem.getOrderItem() instanceof DiscreteOrderItem) {
            DiscreteOrderItem doi = (DiscreteOrderItem) promotableOrderItem.getOrderItem();

            if ((offerPriceData.getIdentifierType().equals(OfferPriceDataIdentifierType.PRODUCT_ID)
                    && Objects.equals(Long.valueOf(offerPriceData.getIdentifierValue()), doi.getProduct().getId()))
                    || (offerPriceData.getIdentifierType().equals(OfferPriceDataIdentifierType.SKU_ID)
                    && Objects.equals(Long.valueOf(offerPriceData.getIdentifierValue()), doi.getSku().getId()))
                    || (offerPriceData.getIdentifierType().equals(OfferPriceDataIdentifierType.PRODUCT_EXTERNAL_ID)
                    && Objects.equals(String.valueOf(offerPriceData.getIdentifierValue()), doi.getProduct().getExternalId()))
                    || (offerPriceData.getIdentifierType().equals(OfferPriceDataIdentifierType.SKU_EXTERNAL_ID)
                    && Objects.equals(String.valueOf(offerPriceData.getIdentifierValue()), doi.getSku().getExternalId()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Money computeRetailAdjustmentValue(PromotableCandidateFulfillmentGroupOffer promotableCandidateFulfillmentGroupOffer, PromotableFulfillmentGroup promotableFulfillmentGroup) {
        return computeAdjustmentValue(promotableCandidateFulfillmentGroupOffer, promotableFulfillmentGroup, false);
    }

    @Override
    public Money computeSalesAdjustmentValue(PromotableCandidateFulfillmentGroupOffer promotableCandidateFulfillmentGroupOffer, PromotableFulfillmentGroup promotableFulfillmentGroup) {
        return computeAdjustmentValue(promotableCandidateFulfillmentGroupOffer, promotableFulfillmentGroup, true);
    }

    @Override
    public Money computeAdjustmentValue(PromotableCandidateFulfillmentGroupOffer promotableCandidateFulfillmentGroupOffer, PromotableFulfillmentGroup promotableFulfillmentGroup, boolean allowSalePrice) {
        Money currentPriceDetailValue = promotableFulfillmentGroup.calculatePriceWithAdjustments(allowSalePrice);
        Offer offer = promotableCandidateFulfillmentGroupOffer.getOffer();
        BroadleafCurrency currency = promotableFulfillmentGroup.getFulfillmentGroup().getOrder().getCurrency();
        BigDecimal offerUnitValue = determineOfferUnitValue(offer, null);
        OfferDiscountType discountType = offer.getDiscountType();
        return computeAdjustmentValue(currentPriceDetailValue, offerUnitValue, currency, discountType, promotableCandidateFulfillmentGroupOffer);
    }

    @Override
    public Money computeRetailAdjustmentValue(PromotableCandidateItemOffer promotableCandidateItemOffer, PromotableOrderItemPriceDetail orderItemPriceDetail) {
        return computeAdjustmentValue(promotableCandidateItemOffer, orderItemPriceDetail, false);
    }

    @Override
    public Money computeSalesAdjustmentValue(PromotableCandidateItemOffer promotableCandidateItemOffer, PromotableOrderItemPriceDetail orderItemPriceDetail) {
        if (promotableCandidateItemOffer.getOffer().getApplyDiscountToSalePrice()) {
            return computeAdjustmentValue(promotableCandidateItemOffer, orderItemPriceDetail, true);
        } else {
            //If we aren't evaluating the sale price then the adjustment will be zero
            PromotableOrderItem promotableOrderItem = orderItemPriceDetail.getPromotableOrderItem();
            BroadleafCurrency currency = promotableOrderItem.getCurrency();
            return new Money(currency);
        }
    }

    @Override
    public Money computeAdjustmentValue(PromotableCandidateItemOffer promotableCandidateItemOffer, PromotableOrderItemPriceDetail orderItemPriceDetail, boolean allowSalePrice) {
        Money currentPriceDetailValue = orderItemPriceDetail.calculateItemUnitPriceWithAdjustments(allowSalePrice);
        PromotableOrderItem promotableOrderItem = orderItemPriceDetail.getPromotableOrderItem();
        BroadleafCurrency currency = promotableOrderItem.getCurrency();
        Tuple<OfferDiscountType, BigDecimal> discountVariables = computeDiscountVariables(promotableCandidateItemOffer, promotableOrderItem, promotableCandidateItemOffer.calculateTargetQuantityForTieredOffer());
        return computeAdjustmentValue(currentPriceDetailValue, discountVariables.getSecond(), currency, discountVariables.getFirst(), promotableCandidateItemOffer);
    }

    @Override
    public Money calculateSavingsForOrderItem(PromotableCandidateItemOffer promotableCandidateItemOffer, PromotableOrderItem promotableOrderItem, int qtyToReceiveSavings) {
        Offer offer = promotableCandidateItemOffer.getOffer();
        Money originalPrice = promotableOrderItem.getPriceBeforeAdjustments(offer.getApplyDiscountToSalePrice());
        BroadleafCurrency currency = promotableOrderItem.getCurrency();
        Tuple<OfferDiscountType, BigDecimal> discountVariables = computeDiscountVariables(promotableCandidateItemOffer, promotableOrderItem, qtyToReceiveSavings);
        Money savings = computeAdjustmentValue(originalPrice, discountVariables.getSecond(), currency, discountVariables.getFirst(), promotableCandidateItemOffer);
        return savings.multiply(qtyToReceiveSavings);
    }

    /**
     * Computes the discount type and unit value for the given PromotableOrderItem.
     * @param promotableCandidateItemOffer
     * @param promotableOrderItem
     * @param quantity
     * @return
     */
    protected Tuple<OfferDiscountType, BigDecimal> computeDiscountVariables(PromotableCandidateItemOffer promotableCandidateItemOffer, PromotableOrderItem promotableOrderItem, int quantity) {
        Offer offer = promotableCandidateItemOffer.getOffer();
        BigDecimal offerUnitValue;
        OfferDiscountType discountType;
        if (BooleanUtils.isTrue(offer.getUseListForDiscounts()) && MapUtils.isNotEmpty(promotableCandidateItemOffer.getCandidateFixedTargetsMap())) {
            OfferPriceData offerPriceData = findMatchingOfferPriceData(promotableCandidateItemOffer, promotableOrderItem);
            discountType = offerPriceData.getDiscountType();
            offerUnitValue = offerPriceData.getAmount();
        } else {
            discountType = offer.getDiscountType();
            offerUnitValue = determineOfferUnitValue(offer, promotableCandidateItemOffer.calculateTargetQuantityForTieredOffer());
        }
        return new Tuple<>(discountType, offerUnitValue);
    }

    protected OfferPriceData findMatchingOfferPriceData(PromotableCandidateItemOffer promotableCandidateItemOffer, PromotableOrderItem orderItem) {
        for (OfferPriceData offerPriceData : promotableCandidateItemOffer.getCandidateFixedTargetsMap().keySet()) {
            if (itemMatchesOfferPriceData(offerPriceData, orderItem)) {
                return offerPriceData;
            }
        }
        return null;
    }

    protected Money computeAdjustmentValue(Money currentPriceDetailValue, BigDecimal offerUnitValue, BroadleafCurrency currency, OfferDiscountType discountType, PromotionRounding rounding) {
        Money adjustmentValue;
        if (currency != null) {
            adjustmentValue = new Money(currency);
        } else {
            adjustmentValue = new Money();
        }

        if (OfferDiscountType.AMOUNT_OFF.equals(discountType)) {
            adjustmentValue = new Money(offerUnitValue, currency);
        }

        if (OfferDiscountType.FIX_PRICE.equals(discountType)) {
            adjustmentValue = currentPriceDetailValue.subtract(new Money(offerUnitValue, currency));
        }

        if (OfferDiscountType.PERCENT_OFF.equals(discountType)) {
            BigDecimal offerValue =
                    currentPriceDetailValue.getAmount().multiply(offerUnitValue.divide(
                            new BigDecimal("100"), 5, rounding.getRoundingMode()));
            int scale = 2;
            if (currency != null) {
                // default scale to currency if currency is not null
                scale = currency.getJavaCurrency().getDefaultFractionDigits();
            }
            if (rounding.getRoundingScale() != null) {
                // override scale from rounding settings if set
                scale = rounding.getRoundingScale();
            }
            adjustmentValue = new Money(offerValue, currency, scale, rounding.getRoundingMode());
            if (rounding.getRoundingScale() != null) {
                adjustmentValue = Money.trimUnnecessaryScaleToCurrency(adjustmentValue);
            }
        }

        if (currentPriceDetailValue.lessThan(adjustmentValue)) {
            adjustmentValue = currentPriceDetailValue;
        }
        return adjustmentValue;
    }

    @SuppressWarnings("unchecked")
    protected BigDecimal determineOfferUnitValue(Offer offer, Integer quantityForTieredOffer) {
        if (offer instanceof AdvancedOffer) {
            AdvancedOffer advancedOffer = (AdvancedOffer) offer;
            if (advancedOffer.isTieredOffer()) {
                List<OfferTier> offerTiers = advancedOffer.getOfferTiers();

                Collections.sort(offerTiers, new BeanComparator("minQuantity"));

                OfferTier maxTier = null;
                //assuming that promotableOffer.getOffer()).getOfferTiers() is sorted already
                for (OfferTier currentTier : offerTiers) {

                    if (quantityForTieredOffer >= currentTier.getMinQuantity()) {
                        maxTier = currentTier;
                    } else {
                        break;
                    }
                }

                if (maxTier != null) {
                    return maxTier.getAmount();
                }

                if (OfferDiscountType.FIX_PRICE.equals(offer.getDiscountType())) {
                    // Choosing an arbitrary large value.    The retail / sale price will be less than this,
                    // so the offer will not get selected.
                    return BigDecimal.valueOf(Integer.MAX_VALUE);
                } else {
                    return BigDecimal.ZERO;
                }
            }
        }
        return offer.getValue();
    }
}
