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
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.util.money.Money;

@Entity
@Table(name = "BLC_ORDER_ADJUSTMENT")
public class OrderAdjustmentImpl implements Serializable, OrderAdjustment {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ADJUSTMENT_ID")
    private Long id;

    @ManyToOne(targetEntity = OrderImpl.class)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne(targetEntity = OfferImpl.class)
    @JoinColumn(name = "OFFER_ID")
    private Offer offer;

    @Column(name = "ADJUSTMENT_REASON")
    private String reason;

    @Column(name = "ADJUSTMENT_VALUE")
    private BigDecimal value;

    public OrderAdjustmentImpl(Order order, Offer offer, String reason){
        this.order = order;
        this.offer = offer;
        this.reason = reason;
        computeAdjustmentValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Money getValue() {
        return value == null ? null : new Money(value);
    }

    /*
     * Calculates the value of the adjustment
     */
    public void computeAdjustmentValue() {
        if (offer != null && order != null) {

            Money adjustmentPrice = order.getAdjustmentPrice(); // get the current price of the item with all adjustments
            if (adjustmentPrice == null) {
                adjustmentPrice = order.getSubTotal();
            }

            if (offer.getDiscountType() == OfferDiscountType.AMOUNT_OFF ) {
                value = offer.getValue().getAmount();
            }
            if (offer.getDiscountType() == OfferDiscountType.FIX_PRICE) {
                value = adjustmentPrice.subtract(offer.getValue()).getAmount();
            }
            if (offer.getDiscountType() == OfferDiscountType.PERCENT_OFF) {
                value = adjustmentPrice.multiply(offer.getValue().divide(new BigDecimal("100")).getAmount()).getAmount();
            }
        }
    }


}
