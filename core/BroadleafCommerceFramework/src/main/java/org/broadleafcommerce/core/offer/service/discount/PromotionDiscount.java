package org.broadleafcommerce.core.offer.service.discount;

import java.io.Serializable;

import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.money.Money;

/**
 * Records the usage of this item as qualifier or target of
 * the promotion.   The discount amount will be 0 if this
 * item was only used as a qualifier.
 */
public class PromotionDiscount implements Serializable{ 
    private static final long serialVersionUID = 1L;
    
    private CandidateItemOffer candidateItemOffer;
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
	
	public CandidateItemOffer getCandidateItemOffer() {
		return candidateItemOffer;
	}

	public void setCandidateItemOffer(CandidateItemOffer candidateItemOffer) {
		this.candidateItemOffer = candidateItemOffer;
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