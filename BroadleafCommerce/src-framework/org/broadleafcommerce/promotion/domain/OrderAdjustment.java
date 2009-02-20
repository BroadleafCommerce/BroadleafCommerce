package org.broadleafcommerce.promotion.domain;

import java.io.Serializable;

import org.broadleafcommerce.order.domain.Order;

//@Entity
//@Table(name = "BLC_ORDER_ADJUSTMENTS")
public class OrderAdjustment implements Serializable {

    public static final long serialVersionUID = 1L;

    // @Id
    // @GeneratedValue
    // @Column(name = "ORDER_ADJUSTMENT_ID")
    private Long id;

    // @ManyToOne
    // @JoinColumn(name = "SALES_ORDER_ID")
    private Order order;

    // @ManyToOne
    // @JoinColumn(name = "PROMOTIONAL_ID")
    private Promotion promotion;

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

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }
}
