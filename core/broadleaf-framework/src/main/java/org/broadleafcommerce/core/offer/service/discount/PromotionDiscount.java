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
    
    public boolean isFinalized() {
        return quantity == finalizedQuantity;
    }

}
