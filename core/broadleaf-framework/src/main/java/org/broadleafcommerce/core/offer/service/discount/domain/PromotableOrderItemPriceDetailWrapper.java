/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.offer.service.discount.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;

import java.util.List;

/**
 * Modules that extend offer engine behavior can benefit from a wrapped PromotableOrderItemPriceDetail.
 * 
 * @author bpolster
 *
 */
public class PromotableOrderItemPriceDetailWrapper implements PromotableOrderItemPriceDetail {

    private PromotableOrderItemPriceDetail detail;

    public PromotableOrderItemPriceDetailWrapper(PromotableOrderItemPriceDetail wrappedDetail) {
        detail = wrappedDetail;
    }

    public void addCandidateItemPriceDetailAdjustment(PromotableOrderItemPriceDetailAdjustment itemAdjustment) {
        detail.addCandidateItemPriceDetailAdjustment(itemAdjustment);
    }

    public List<PromotableOrderItemPriceDetailAdjustment> getCandidateItemAdjustments() {
        return detail.getCandidateItemAdjustments();
    }

    public boolean hasNonCombinableAdjustments() {
        return detail.hasNonCombinableAdjustments();
    }

    public boolean isTotalitarianOfferApplied() {
        return detail.isTotalitarianOfferApplied();
    }

    public boolean isNonCombinableOfferApplied() {
        return detail.isNonCombinableOfferApplied();
    }

    public void chooseSaleOrRetailAdjustments() {
        detail.chooseSaleOrRetailAdjustments();
    }

    public void removeAllAdjustments() {
        detail.removeAllAdjustments();
    }

    public List<PromotionDiscount> getPromotionDiscounts() {
        return detail.getPromotionDiscounts();
    }

    public List<PromotionQualifier> getPromotionQualifiers() {
        return detail.getPromotionQualifiers();
    }

    public int getQuantity() {
        return detail.getQuantity();
    }

    public void setQuantity(int quantity) {
        detail.setQuantity(quantity);
    }

    public PromotableOrderItem getPromotableOrderItem() {
        return detail.getPromotableOrderItem();
    }

    public int getQuantityAvailableToBeUsedAsQualifier(PromotableCandidateItemOffer itemOffer) {
        return detail.getQuantityAvailableToBeUsedAsQualifier(itemOffer);
    }

    public int getQuantityAvailableToBeUsedAsTarget(PromotableCandidateItemOffer itemOffer) {
        return detail.getQuantityAvailableToBeUsedAsTarget(itemOffer);
    }

    public PromotionQualifier addPromotionQualifier(PromotableCandidateItemOffer itemOffer, OfferItemCriteria itemCriteria, int qtyToMarkAsQualifier) {
        return detail.addPromotionQualifier(itemOffer, itemCriteria, qtyToMarkAsQualifier);
    }

    public void addPromotionDiscount(PromotableCandidateItemOffer itemOffer, OfferItemCriteria itemCriteria, int qtyToMarkAsTarget) {
        detail.addPromotionDiscount(itemOffer, itemCriteria, qtyToMarkAsTarget);
    }

    public Money calculateItemUnitPriceWithAdjustments(boolean allowSalePrice) {
        return detail.calculateItemUnitPriceWithAdjustments(allowSalePrice);
    }

    public void finalizeQuantities() {
        detail.finalizeQuantities();
    }

    public void clearAllNonFinalizedQuantities() {
        detail.clearAllNonFinalizedQuantities();
    }

    public String buildDetailKey() {
        return detail.buildDetailKey();
    }

    public Money getFinalizedTotalWithAdjustments() {
        return detail.getFinalizedTotalWithAdjustments();
    }

    public Money calculateTotalAdjustmentValue() {
        return detail.calculateTotalAdjustmentValue();
    }

    public PromotableOrderItemPriceDetail splitIfNecessary() {
        return detail.splitIfNecessary();
    }

    public boolean useSaleAdjustments() {
        return detail.useSaleAdjustments();
    }

    public boolean isAdjustmentsFinalized() {
        return detail.isAdjustmentsFinalized();
    }

    public void setAdjustmentsFinalized(boolean adjustmentsFinalized) {
        detail.setAdjustmentsFinalized(adjustmentsFinalized);
    }

    @Override
    public PromotableOrderItemPriceDetail shallowCopy() {
        return detail.shallowCopy();
    }

    @Override
    public PromotableOrderItemPriceDetail copyWithFinalizedData() {
        return detail.copyWithFinalizedData();
    }

}
