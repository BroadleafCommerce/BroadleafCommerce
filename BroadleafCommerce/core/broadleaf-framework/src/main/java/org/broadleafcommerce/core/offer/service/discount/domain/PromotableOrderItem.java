package org.broadleafcommerce.core.offer.service.discount.domain;

import java.util.List;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.money.Money;

public interface PromotableOrderItem {
	 
	 /**
     * Adds the adjustment to the order item's adjustment list and discounts the
     * order item's adjustment price by the value of the adjustment.
     * @param orderItemAdjustment
     */
    public void addOrderItemAdjustment(PromotableOrderItemAdjustment orderItemAdjustment);
    
    public Money getAdjustmentPrice();
    
    public void setAdjustmentPrice(Money adjustmentPrice);
    
    public boolean isNotCombinableOfferApplied();
    
    public boolean isHasOrderItemAdjustments();
    
    public List<PromotionDiscount> getPromotionDiscounts();
    
    public void setPromotionDiscounts(List<PromotionDiscount> promotionDiscounts);
    
    public List<PromotionQualifier> getPromotionQualifiers();
    
    public void setPromotionQualifiers(List<PromotionQualifier> promotionQualifiers);
    
    public int getQuantityAvailableToBeUsedAsQualifier(Offer promotion);
    
    public int getQuantityAvailableToBeUsedAsTarget(Offer promotion);
    
    public void addPromotionQualifier(PromotableCandidateItemOffer candidatePromotion, OfferItemCriteria itemCriteria, int quantity);
	
	public void addPromotionDiscount(PromotableCandidateItemOffer candidatePromotion, OfferItemCriteria itemCriteria, int quantity);
	
	public void clearAllNonFinalizedQuantities();
	
	public void clearAllDiscount();
	
	public void clearAllQualifiers();
	
	public void finalizeQuantities();
	
	public List<PromotableOrderItem> split();
	
	public DiscreteOrderItem getDelegate();
	
	public void reset();
	
	public PromotionQualifier lookupOrCreatePromotionQualifier(PromotableCandidateItemOffer candidatePromotion);
	
	public PromotionDiscount lookupOrCreatePromotionDiscount(PromotableCandidateItemOffer candidatePromotion);
	
	public void clearAllNonFinalizedDiscounts();
	
	public void clearAllNonFinalizedQualifiers();
	
	public int getPromotionDiscountMismatchQuantity();
	
	public void computeAdjustmentPrice();
	
	public int removeAllAdjustments();
	
	public void assignFinalPrice();
	
	public Money getCurrentPrice();
	
	public int getQuantity();
	
	public void setQuantity(int quantity);
	
	public Sku getSku();
	
	public Money getPriceBeforeAdjustments(boolean allowSalesPrice);
	
	public Money getSalePrice();
	
	public Money getRetailPrice();
	
	public void addCandidateItemOffer(PromotableCandidateItemOffer candidateItemOffer);
	
	public PromotableOrderItem clone();
}
