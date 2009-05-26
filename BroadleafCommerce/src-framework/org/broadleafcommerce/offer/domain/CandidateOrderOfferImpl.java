package org.broadleafcommerce.offer.domain;

import java.math.BigDecimal;

import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.util.money.Money;

public class CandidateOrderOfferImpl implements CandidateOrderOffer {

	private Offer offer;
	private Order order;
    private Money discountedPrice;

	public CandidateOrderOfferImpl(){

	}

	public CandidateOrderOfferImpl(Order order, Offer offer){
		this.order = order;
		this.offer = offer;
	}

	public int getPriority() {
		return offer.getPriority();
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
		discountedPrice = null;  // price needs to be recalculated
	}

	public Money getDiscountedPrice() {
	    if (discountedPrice == null) {
	        computeDiscountAmount();
	    }
		return discountedPrice;
	}

	public Order getOrder() {
		return order;
	}


	public void setOrder(Order order) {
		this.order = order;
        discountedPrice = null;  // price needs to be recalculated
	}

	protected void computeDiscountAmount() {
		if(offer != null && order != null){

			Money priceToUse = order.getSubTotal();

	        if(offer.getDiscountType() == OfferDiscountType.AMOUNT_OFF ){
	            priceToUse.subtract(offer.getValue());
	        }
	        if(offer.getDiscountType() == OfferDiscountType.FIX_PRICE){
	            priceToUse = offer.getValue();
	        }

	        if(offer.getDiscountType() == OfferDiscountType.PERCENT_OFF){
	            priceToUse = priceToUse.subtract(priceToUse.multiply(offer.getValue().divide(new BigDecimal("100")).getAmount()));
	        }
	        discountedPrice = priceToUse;
		}
	}


}
