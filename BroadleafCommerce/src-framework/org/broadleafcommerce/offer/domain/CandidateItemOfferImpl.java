package org.broadleafcommerce.offer.domain;

import java.math.BigDecimal;

import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.util.money.Money;

public class CandidateItemOfferImpl implements CandidateItemOffer {
    private OrderItem orderItem;
    private Offer offer;
    private Money discountedPrice;


    public CandidateItemOfferImpl(){

    }

    public CandidateItemOfferImpl(OrderItem orderItem, Offer offer){
        this.orderItem = orderItem;
        this.offer = offer;
        computeDiscountAmount();
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
        computeDiscountAmount();
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        computeDiscountAmount();

    }

    public Money getDiscountedPrice() {
        computeDiscountAmount();
        return discountedPrice;
    }

    public int getPriority() {
        return offer.getPriority();
    }

    public Offer getOffer() {
        return offer;
    }

    protected void computeDiscountAmount() {
        if(offer != null && orderItem != null){

            Money priceToUse = orderItem.getRetailPrice();
            if (offer.getApplyDiscountToSalePrice()) {
                priceToUse = orderItem.getSalePrice();
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
