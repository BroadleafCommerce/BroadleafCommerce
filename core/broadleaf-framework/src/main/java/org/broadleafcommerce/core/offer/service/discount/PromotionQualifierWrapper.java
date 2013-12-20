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
