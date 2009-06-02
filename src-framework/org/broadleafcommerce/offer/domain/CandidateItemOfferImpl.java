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
package org.broadleafcommerce.offer.domain;

import java.math.BigDecimal;

import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.util.money.Money;

public class CandidateItemOfferImpl implements CandidateItemOffer {
    private OrderItem orderItem;
    private Offer offer;
    private Money discountedPrice;

    public CandidateItemOfferImpl(){

    }

    public CandidateItemOfferImpl(OrderItem orderItem, Offer offer){
        this.orderItem = orderItem;
        this.offer = offer;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
        discountedPrice = null;  // price needs to be recalculated
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        discountedPrice = null;  // price needs to be recalculated
    }

    // returns null if no order item or offer
    public Money getDiscountedPrice() {
        if (discountedPrice == null) {
            computeDiscountPrice();
        }
        return discountedPrice;
    }

    public int getPriority() {
        return offer.getPriority();
    }

    public Offer getOffer() {
        return offer;
    }

    protected void computeDiscountPrice() {
        if (offer != null && orderItem != null){

            Money priceToUse = orderItem.getRetailPrice();
/*            if (offer.getApplyDiscountToSalePrice()) {
                priceToUse = orderItem.getSalePrice();
            }
*/
            if (offer.getDiscountType() == OfferDiscountType.AMOUNT_OFF ) {
                priceToUse.subtract(offer.getValue());
            }
            if (offer.getDiscountType() == OfferDiscountType.FIX_PRICE) {
                priceToUse = offer.getValue();
            }

            if (offer.getDiscountType() == OfferDiscountType.PERCENT_OFF) {
                priceToUse = priceToUse.subtract(priceToUse.multiply(offer.getValue().divide(new BigDecimal("100")).getAmount()));
            }
            discountedPrice = priceToUse;
        }
    }

}
