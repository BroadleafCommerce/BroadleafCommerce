package org.broadleafcommerce.order.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@DiscriminatorColumn(name="TYPE")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULLFILLMENT_GROUP_ITEM")
public class FullfillmentGroupItemImpl implements FullfillmentGroupItem, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "FULLFILLMENT_GROUP_ID")
    private Long fullfillmentGroupId;

    @OneToOne(targetEntity=OrderItemImpl.class)
    @JoinColumn(name = "ORDER_ID")
    private OrderItem orderItem;

    @Column(name = "QUANTITY")
    private int quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFullfillmentGroupId() {
        return fullfillmentGroupId;
    }

    public void setFullfillmentGroupId(Long fullfillmentGroupId) {
        this.fullfillmentGroupId = fullfillmentGroupId;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
