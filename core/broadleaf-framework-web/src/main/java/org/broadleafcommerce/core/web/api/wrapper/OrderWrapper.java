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

package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.order.domain.Order;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around Order.
 *
 * User: Elbert Bautista
 * Date: 4/10/12
 */
@XmlRootElement(name = "order")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OrderWrapper extends BaseWrapper implements APIWrapper<Order> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String status;

    @XmlElement
    protected MoneyWrapper totalTax;

    @XmlElement
    protected MoneyWrapper totalShipping;

    @XmlElement
    protected MoneyWrapper subTotal;

    @XmlElement
    protected MoneyWrapper total;

    @Override
    public void wrap(Order model, HttpServletRequest request) {
        this.id = model.getId();
        this.status = model.getStatus().getType();

        MoneyWrapper totalTaxWrapper = (MoneyWrapper) context.getBean(MoneyWrapper.class.getName());
        totalTaxWrapper.wrap(model.getTotalTax(), request);
        this.totalTax = totalTaxWrapper;

        MoneyWrapper totalShippingWrapper = (MoneyWrapper) context.getBean(MoneyWrapper.class.getName());
        totalShippingWrapper.wrap(model.getTotalShipping(), request);
        this.totalShipping = totalShippingWrapper;

        MoneyWrapper subTotalWrapper = (MoneyWrapper) context.getBean(MoneyWrapper.class.getName());
        subTotalWrapper.wrap(model.getSubTotal(), request);
        this.subTotal = subTotalWrapper;

        MoneyWrapper totalWrapper = (MoneyWrapper) context.getBean(MoneyWrapper.class.getName());
        totalWrapper.wrap(model.getTotal(), request);
        this.total = totalWrapper;
    }
}
