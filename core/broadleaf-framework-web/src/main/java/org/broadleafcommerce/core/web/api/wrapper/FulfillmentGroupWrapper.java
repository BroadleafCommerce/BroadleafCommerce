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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around FulfillmentGroup.
 *
 * User: Elbert Bautista
 * Date: 4/10/12
 */
@XmlRootElement(name = "fulfillmentGroup")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FulfillmentGroupWrapper extends BaseWrapper implements APIWrapper<FulfillmentGroup> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected Money total;

    @XmlElement
    protected AddressWrapper address;

    @XmlElement
    protected OrderWrapper order;

    @Override
    public void wrap(FulfillmentGroup model, HttpServletRequest request) {
        this.id = model.getId();
        this.total = model.getTotal();

        AddressWrapper addressWrapper = (AddressWrapper) context.getBean(AddressWrapper.class.getName());
        addressWrapper.wrap(model.getAddress(), request);
        this.address = addressWrapper;

        OrderWrapper orderWrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
        orderWrapper.wrap(model.getOrder(), request);
        this.order = orderWrapper;
    }
}
