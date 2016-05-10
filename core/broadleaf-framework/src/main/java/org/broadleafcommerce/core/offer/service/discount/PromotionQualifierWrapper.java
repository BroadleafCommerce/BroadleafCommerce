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


/**
 * Wraps the promotion qualifier.   Serves as a useful starting point for extension.
 * 
 * @author bpolster
 */
public class PromotionQualifierWrapper extends PromotionQualifier {

    private static final long serialVersionUID = 1L;
    
    private PromotionQualifier wrappedQualifier;
    
    public PromotionQualifierWrapper(PromotionQualifier pq) {
        wrappedQualifier = pq;
    }

    public Offer getPromotion() {
        return wrappedQualifier.getPromotion();
    }

    public void setPromotion(Offer promotion) {
        wrappedQualifier.setPromotion(promotion);
    }

    public void setItemCriteria(OfferItemCriteria itemCriteria) {
        wrappedQualifier.setItemCriteria(itemCriteria);
    }

    public int getQuantity() {
        return wrappedQualifier.getQuantity();
    }

    public void setQuantity(int quantity) {
        wrappedQualifier.setQuantity(quantity);
    }

    public void setFinalizedQuantity(int finalizedQuantity) {
        wrappedQualifier.setFinalizedQuantity(finalizedQuantity);
    }

    public PromotionQualifier copy() {
        return wrappedQualifier.copy();
    }

    public boolean equals(Object arg0) {
        return wrappedQualifier.equals(arg0);
    }

    public OfferItemCriteria getItemCriteria() {
        return wrappedQualifier.getItemCriteria();
    }

    public int getFinalizedQuantity() {
        return wrappedQualifier.getFinalizedQuantity();
    }

    public int hashCode() {
        return wrappedQualifier.hashCode();
    }

    public void incrementQuantity(int quantity) {
        wrappedQualifier.incrementQuantity(quantity);
    }

    public void resetQty(int qty) {
        wrappedQualifier.resetQty(qty);
    }

    public PromotionQualifier split(int splitItemQty) {
        return wrappedQualifier.split(splitItemQty);
    }

    public boolean isFinalized() {
        return wrappedQualifier.isFinalized();
    }

    public String toString() {
        return wrappedQualifier.toString();
    }
}
