package org.broadleafcommerce.order.web.model;

import java.math.BigDecimal;
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
    private String promoCode;
    private BigDecimal orderDiscounts;

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

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public BigDecimal getOrderDiscounts() {
        return orderDiscounts;
    }

    public void setOrderDiscounts(BigDecimal orderDiscounts) {
        this.orderDiscounts = orderDiscounts;
    }
}
