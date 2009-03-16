package org.broadleafcommerce.offer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderImpl;

@Entity
@Table(name = "BLC_ORDER_ADJUSTMENT")
public class OrderAdjustment implements Serializable {

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
}
