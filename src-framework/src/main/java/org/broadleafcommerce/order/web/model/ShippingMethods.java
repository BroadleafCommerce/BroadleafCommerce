package org.broadleafcommerce.order.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;

public class ShippingMethods {

    @SuppressWarnings("unchecked")
    private List<FulfillmentGroup> rows =  LazyList.decorate(
            new ArrayList<FulfillmentGroup>(),
            FactoryUtils.instantiateFactory(FulfillmentGroupImpl.class));

    public List<FulfillmentGroup> getRows() {
        return rows;
    }

    public void setRows(List<FulfillmentGroup> rows) {
        this.rows = rows;
    }

}
