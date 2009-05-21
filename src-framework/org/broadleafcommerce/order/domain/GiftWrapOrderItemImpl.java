package org.broadleafcommerce.order.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_GIFTWRAP_ORDER_ITEM")
public class GiftWrapOrderItemImpl extends DiscreteOrderItemImpl implements GiftWrapOrderItem {

    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "giftWrapOrderItem", targetEntity = OrderItemImpl.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<OrderItem> wrappedItems = new ArrayList<OrderItem>();

    public List<OrderItem> getWrappedItems() {
        return wrappedItems;
    }

    public void setWrappedItems(List<OrderItem> wrappedItems) {
        this.wrappedItems = wrappedItems;
    }

}
