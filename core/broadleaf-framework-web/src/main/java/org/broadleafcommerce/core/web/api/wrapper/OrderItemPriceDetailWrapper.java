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

import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * API wrapper to wrap Order Item Price Details.
 * @author Priyesh Patel
 *
 */
@XmlRootElement(name = "orderItemAttribute")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OrderItemPriceDetailWrapper extends BaseWrapper implements
        APIWrapper<OrderItemPriceDetail> {
    
    @XmlElement
    protected Long id;
    
    @XmlElement
    protected Integer quantity;
    @XmlElement(name = "orderItemPriceDetails")
    @XmlElementWrapper(name = "orderItemPriceDetailAdjustment")
    protected List<OrderItemPriceDetailAdjustmentWrapper> orderItemPriceDetailAdjustments = new LinkedList<OrderItemPriceDetailAdjustmentWrapper>();

    @Override
    public void wrap(OrderItemPriceDetail model, HttpServletRequest request) {
        this.id = model.getId();
        this.quantity = model.getQuantity();
        if (!model.getOrderItemPriceDetailAdjustments().isEmpty()) {
            this.orderItemPriceDetailAdjustments = new ArrayList<OrderItemPriceDetailAdjustmentWrapper>();
            for (OrderItemPriceDetailAdjustment orderItemPriceDetail : model.getOrderItemPriceDetailAdjustments()) {
                OrderItemPriceDetailAdjustmentWrapper orderItemPriceDetailAdjustmentWrapper =
                        (OrderItemPriceDetailAdjustmentWrapper) context.getBean(OrderItemPriceDetailAdjustmentWrapper.class.getName());
                orderItemPriceDetailAdjustmentWrapper.wrap(orderItemPriceDetail, request);
                this.orderItemPriceDetailAdjustments.add(orderItemPriceDetailAdjustmentWrapper);
            }
        }
    }
    
}
