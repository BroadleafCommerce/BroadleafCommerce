package org.broadleafcommerce.core.offer.service.candidate;

import java.io.Serializable;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;

/**
 * Records the usage of this item as qualifier or target of
 * the promotion.   The discount amount will be 0 if this
 * item was only used as a qualifier.
 */
public class PromotionQualifier implements Serializable{ 
    private static final long serialVersionUID = 1L;
    
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
	
	public PromotionQualifier copy() {
		PromotionQualifier pq = new PromotionQualifier();
		pq.setItemCriteria(itemCriteria);
		pq.setPromotion(promotion);
		pq.setQuantity(quantity);
		pq.setFinalizedQuantity(finalizedQuantity);
		return pq;
	}
	
	public void resetQty(int qty) {
		quantity = qty;
		finalizedQuantity = qty;
	}

}