/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.order.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;

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
