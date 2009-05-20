package org.broadleafcommerce.offer.domain;

import java.math.BigDecimal;

import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.util.money.Money;

public class CandidateFulfillmentGroupOfferImpl implements
		CandidateFulfillmentGroupOffer {

	private Offer offer;
	private Money discountedPrice;
	private FulfillmentGroup fulfillmentGroup;

	public CandidateFulfillmentGroupOfferImpl(){
		
	}
	
	public CandidateFulfillmentGroupOfferImpl(FulfillmentGroup fulfillmentGroup, Offer offer){
		this.offer = offer;
		this.fulfillmentGroup = fulfillmentGroup;
		computeDiscountAmount();
	}
	
	public Offer getOffer() {
		return offer;
	}

	
	public void setOffer(Offer offer) {
		this.offer = offer;
		computeDiscountAmount();
	}
	public Money getDiscountedPrice() {
		computeDiscountAmount();
		return discountedPrice;
	}

	public FulfillmentGroup getFulfillmentGroup() {
		return fulfillmentGroup;
	}
	public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {		
		this.fulfillmentGroup = fulfillmentGroup;
		computeDiscountAmount();
	}
	
	public int getPriority() {
		return offer.getPriority();
	}
	
	
	protected void computeDiscountAmount() {
		if(offer != null && fulfillmentGroup != null){
			
			Money priceToUse = fulfillmentGroup.getRetailShippingPrice();
			if (offer.getApplyDiscountToSalePrice()) {
				priceToUse = fulfillmentGroup.getSaleShippingPrice();
			}
	
	        if(offer.getDiscountType() == OfferDiscountType.AMOUNT_OFF ){
	            priceToUse.subtract(offer.getValue());
	        }
	        if(offer.getDiscountType() == OfferDiscountType.FIX_PRICE){
	            priceToUse = offer.getValue();
	        }
	
	        if(offer.getDiscountType() == OfferDiscountType.PERCENT_OFF){
	            priceToUse = priceToUse.multiply(offer.getValue().divide(new BigDecimal("100")).getAmount());
	        }
	        discountedPrice = priceToUse;
		}
	}
	
}
