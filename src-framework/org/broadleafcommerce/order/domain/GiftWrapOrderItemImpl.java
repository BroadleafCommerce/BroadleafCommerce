package org.broadleafcommerce.order.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_GIFTWRAP_ORDER_ITEM")
public class GiftWrapOrderItemImpl extends DiscreteOrderItemImpl implements GiftWrapOrderItem {

    private static final long serialVersionUID = 1L;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = OrderItemImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_GIFTWRAP_ORDERITEM_XREF", joinColumns = @JoinColumn(name = "GIFTWRAP_ITEM_ID", referencedColumnName = "ORDER_ITEM_ID", nullable = true), inverseJoinColumns = @JoinColumn(name = "ORDER_ITEM_ID", referencedColumnName = "ORDER_ITEM_ID", nullable = true))
    private List<OrderItem> wrappedItems = new ArrayList<OrderItem>();

    public List<OrderItem> getWrappedItems() {
        return wrappedItems;
    }

    public void setWrappedItems(List<OrderItem> wrappedItems) {
        this.wrappedItems = wrappedItems;
    }

}
