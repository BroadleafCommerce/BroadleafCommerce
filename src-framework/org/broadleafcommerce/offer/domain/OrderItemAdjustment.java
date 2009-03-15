package org.broadleafcommerce.offer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderItemImpl;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_ITEM_ADJUSTMENT")
public class OrderItemAdjustment implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ITEM_ADJUSTMENT_ID")
    private Long id;

    @ManyToOne(targetEntity = OrderItemImpl.class)
    @JoinColumn(name = "ORDER_ITEM_ID")
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "OFFER_ID")
    private Offer offer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

}
