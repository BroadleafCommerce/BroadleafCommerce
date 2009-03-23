package org.broadleafcommerce.offer.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.type.OfferDiscountType;
import org.broadleafcommerce.util.money.Money;

public class CandidateOffer implements ItemOffer {
	private List<Offer> offers = new ArrayList<Offer>();
	private Offer offer;
	private Money discountedPrice;

	public CandidateOffer(Offer offer, Money retailPrice, Money salePrice) {
		this.offer = offer;
		offers.add(offer);
		this.discountedPrice = computeDiscountAmount(salePrice, retailPrice);
	}

	protected Money computeDiscountAmount(Money retailPrice, Money salePrice) {
		Money priceToUse = retailPrice;
		if (offer.getApplyDiscountToSalePrice()) {
			priceToUse = salePrice;
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
        return priceToUse;
	}

	public Money getDiscountedPrice() {
		return discountedPrice;
	}

	public int getPriority() {
		return offer.getPriority();
	}

	public List<Offer> getOffers() {
		return offers;
	}
	
	public Offer getOffer() {
		return offer;
	}
}
