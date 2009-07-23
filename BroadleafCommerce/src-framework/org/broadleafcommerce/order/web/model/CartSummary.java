package org.broadleafcommerce.order.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.broadleafcommerce.order.domain.FulfillmentGroup;

public class CartSummary {

    @SuppressWarnings("unchecked")
    private List<CartOrderItem> rows =  LazyList.decorate(
            new ArrayList<CartOrderItem>(),
            FactoryUtils.instantiateFactory(CartOrderItem.class));
    private List<FulfillmentGroup> fulfillmentGroups = new ArrayList<FulfillmentGroup>();

    public List<CartOrderItem> getRows() {
        return rows;
    }

    public void setRows(List<CartOrderItem> rows) {
        this.rows = rows;
    }

    public List<FulfillmentGroup> getFulfillmentGroups() {
        return fulfillmentGroups;
    }

    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups) {
        this.fulfillmentGroups = fulfillmentGroups;
    }

}
