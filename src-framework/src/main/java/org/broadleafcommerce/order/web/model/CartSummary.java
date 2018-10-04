package org.broadleafcommerce.order.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;

public class CartSummary {

    @SuppressWarnings("unchecked")
    private List<CartOrderItem> rows =  LazyList.decorate(
            new ArrayList<CartOrderItem>(),
            FactoryUtils.instantiateFactory(CartOrderItem.class));
    private FulfillmentGroup fulfillmentGroup = new FulfillmentGroupImpl();

    public CartSummary() {
        fulfillmentGroup.setMethod("standard");
    }

    public List<CartOrderItem> getRows() {
        return rows;
    }

    public void setRows(List<CartOrderItem> rows) {
        this.rows = rows;
    }

    public FulfillmentGroup getFulfillmentGroup() {
        return fulfillmentGroup;
    }

    public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        this.fulfillmentGroup = fulfillmentGroup;
    }

}
