package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.broadleafcommerce.catalog.domain.Sku;

//@Entity
//@Table(name = "SC_ORDER_ITEM")
public class BroadleafOrderItem implements OrderItem, Serializable {

    private static final long serialVersionUID = 1L;

    // @Id
    // @GeneratedValue
    // @Column(name = "ORDER_ITEM_ID")
    private Long id;

    // @ManyToOne
    // @JoinColumn(name = "SKU_ID", nullable=false)
    private Sku sku;

    // @ManyToOne
    // @JoinColumn(name = "SC_ORDER_ID", nullable = false)
    private Order order;

    // @Column(name = "FINAL_PRICE")
    private BigDecimal finalPrice;

    // @Column(name = "QUANTITY")
    private int quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
