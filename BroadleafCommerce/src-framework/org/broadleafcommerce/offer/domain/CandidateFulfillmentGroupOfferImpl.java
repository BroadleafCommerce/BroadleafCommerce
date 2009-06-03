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

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.util.money.Money;

@Entity
@Table(name = "BLC_CANDIDATE_FG_OFFER")
public class CandidateFulfillmentGroupOfferImpl implements Serializable,CandidateFulfillmentGroupOffer {
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "CANDIDATE_FG_OFFER_ID")
    private Long id;

    @ManyToOne(targetEntity = FulfillmentGroupImpl.class)
    @JoinColumn(name = "FULFILLMENT_GROUP_ID")
    private FulfillmentGroup fulfillmentGroup;

    @ManyToOne(targetEntity = OfferImpl.class)
    @JoinColumn(name = "OFFER_ID")
    private Offer offer;

    @Column(name = "DISCOUNTED_PRICE")
    private BigDecimal discountedPrice;

	public CandidateFulfillmentGroupOfferImpl(){

	}

	public CandidateFulfillmentGroupOfferImpl(FulfillmentGroup fulfillmentGroup, Offer offer){
		this.offer = offer;
		this.fulfillmentGroup = fulfillmentGroup;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

 	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
		discountedPrice = null;
	}
	public Money getDiscountedPrice() {
        if (discountedPrice == null) {
            computeDiscountedPrice();
        }
        return discountedPrice == null ? null : new Money(discountedPrice);
	}

	public FulfillmentGroup getFulfillmentGroup() {
		return fulfillmentGroup;
	}
	public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
		this.fulfillmentGroup = fulfillmentGroup;
		discountedPrice = null;
	}

	public int getPriority() {
		return offer.getPriority();
	}


	protected void computeDiscountedPrice() {
		if (offer != null && fulfillmentGroup != null){

		    if (fulfillmentGroup.getRetailShippingPrice() != null) {
                Money priceToUse = fulfillmentGroup.getRetailShippingPrice();

    	        if(offer.getDiscountType() == OfferDiscountType.AMOUNT_OFF ){
    	            priceToUse.subtract(offer.getValue());
    	        } else if (offer.getDiscountType() == OfferDiscountType.FIX_PRICE){
    	            priceToUse = offer.getValue();
    	        } else if (offer.getDiscountType() == OfferDiscountType.PERCENT_OFF){
    	            priceToUse = priceToUse.subtract(priceToUse.multiply(offer.getValue().divide(new BigDecimal("100")).getAmount()));
    	        }
                if (priceToUse.lessThan(new Money(0))) {
                    priceToUse = new Money(0);
                }
                discountedPrice = priceToUse.getAmount();
		    }
		}
	}

}
