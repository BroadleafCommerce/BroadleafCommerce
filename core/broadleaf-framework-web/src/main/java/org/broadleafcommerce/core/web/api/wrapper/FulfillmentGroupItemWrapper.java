/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.TaxDetail;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around FulfillmentGroupItem.
 *
 * User: Elbert Bautista
 * Date: 4/10/12
 */
@XmlRootElement(name = "fulfillmentGroupItem")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FulfillmentGroupItemWrapper extends BaseWrapper implements APIWrapper<FulfillmentGroupItem>, APIUnwrapper<FulfillmentGroupItemRequest> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected Long fulfillmentGroupId;

    @XmlElement
    protected Long orderItemId;

    @XmlElement
    protected Money totalTax;

    @XmlElement
    protected Integer quantity;

    @XmlElement
    protected Money totalItemAmount;

    @XmlElement(name = "taxDetail")
    @XmlElementWrapper(name = "taxDetails")
    protected List<TaxDetailWrapper> taxDetails;

    @Override
    public void wrapDetails(FulfillmentGroupItem model, HttpServletRequest request) {
        this.id = model.getId();

        if (model.getFulfillmentGroup() != null) {
            this.fulfillmentGroupId = model.getFulfillmentGroup().getId();
        }

        if (model.getOrderItem() != null) {
            this.orderItemId = model.getOrderItem().getId();
        }

        this.totalTax = model.getTotalTax();
        this.quantity = model.getQuantity();
        this.totalItemAmount = model.getTotalItemAmount();

        List<TaxDetail> taxes = model.getTaxes();
        if (taxes != null && !taxes.isEmpty()) {
            this.taxDetails = new ArrayList<TaxDetailWrapper>();
            for (TaxDetail detail : taxes) {
                TaxDetailWrapper taxDetailWrapper = (TaxDetailWrapper) context.getBean(TaxDetailWrapper.class.getName());
                taxDetailWrapper.wrapSummary(detail, request);
                this.taxDetails.add(taxDetailWrapper);
            }
        }
    }

    @Override
    public void wrapSummary(FulfillmentGroupItem model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public FulfillmentGroupItemRequest unwrap(HttpServletRequest request, ApplicationContext appContext) {
        OrderItemService orderItemService = (OrderItemService) appContext.getBean("blOrderItemService");
        OrderItem orderItem = orderItemService.readOrderItemById(this.orderItemId);
        if (orderItem != null) {
            FulfillmentGroupItemRequest fulfillmentGroupItemRequest = new FulfillmentGroupItemRequest();
            fulfillmentGroupItemRequest.setOrderItem(orderItem);
            fulfillmentGroupItemRequest.setQuantity(this.quantity);
            return fulfillmentGroupItemRequest;
        }

        return null;
    }
}
