/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
//            if (offer.getApplyDiscountToSalePrice()) {
//                priceToUse = fulfillmentGroup.getSaleShippingPrice();

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
