package org.broadleafcommerce.order.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

public class CartSummary {

    @SuppressWarnings("unchecked")
    private List<CartOrderItem> rows =  LazyList.decorate(
            new ArrayList<CartOrderItem>(),
            FactoryUtils.instantiateFactory(CartOrderItem.class));

    public List<CartOrderItem> getRows() {
        return rows;
    }

    public void setRows(List<CartOrderItem> rows) {
        this.rows = rows;
    }

}
