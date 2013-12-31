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

package org.broadleafcommerce.core.offer.service.discount;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;

import java.io.Serializable;

/**
 * Records the usage of this item as qualifier or target of
 * the promotion.   The discount amount will be 0 if this
 * item was only used as a qualifier.
 * 
 * @author jfischer
 */
public class PromotionDiscount implements Serializable{ 
    private static final long serialVersionUID = 1L;
    
    private PromotableCandidateItemOffer candidateItemOffer;
    private Offer promotion;
    private OfferItemCriteria itemCriteria;
    private int quantity;
    private int finalizedQuantity;

    
    public Offer getPromotion() {
        return promotion;
    }
    
    public void setPromotion(Offer promotion) {
        this.promotion = promotion;
    }
    
    public OfferItemCriteria getItemCriteria() {
        return itemCriteria;
    }
    
    public void setItemCriteria(OfferItemCriteria itemCriteria) {
        this.itemCriteria = itemCriteria;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getFinalizedQuantity() {
        return finalizedQuantity;
    }

    public void setFinalizedQuantity(int finalizedQuantity) {
        this.finalizedQuantity = finalizedQuantity;
    }
    
    public void incrementQuantity(int quantity) {
        this.quantity = this.quantity + quantity;
    }
    
    public PromotableCandidateItemOffer getCandidateItemOffer() {
        return candidateItemOffer;
    }

    public void setCandidateItemOffer(PromotableCandidateItemOffer candidateItemOffer) {
        this.candidateItemOffer = candidateItemOffer;
    }

    public PromotionDiscount split(int splitQty) {
        PromotionDiscount returnDiscount = copy();
        int originalQty = finalizedQuantity;

        setFinalizedQuantity(splitQty);
        setQuantity(splitQty);

        int newDiscountQty = originalQty - splitQty;
        if (newDiscountQty == 0) {
            return null;
        } else {
            returnDiscount.setQuantity(newDiscountQty);
            returnDiscount.setFinalizedQuantity(newDiscountQty);
        }
        return returnDiscount;
    }

    public PromotionDiscount copy() {
        PromotionDiscount pd = new PromotionDiscount();
        pd.setItemCriteria(itemCriteria);
        pd.setPromotion(promotion);
        pd.setQuantity(quantity);
        pd.setFinalizedQuantity(finalizedQuantity);
        pd.setCandidateItemOffer(candidateItemOffer);
        return pd;
    }
    
    public void resetQty(int qty) {
        quantity = qty;
        finalizedQuantity = qty;
    }
    
}