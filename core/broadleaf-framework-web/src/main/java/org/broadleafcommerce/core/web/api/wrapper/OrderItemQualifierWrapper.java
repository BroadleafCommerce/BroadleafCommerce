/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.order.domain.OrderItemQualifier;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "orderItemQualifier")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OrderItemQualifierWrapper extends BaseWrapper implements APIWrapper<OrderItemQualifier> {

    @XmlElement
    protected Long offerId;

    @XmlElement
    protected Long quantity;

    @Override
    public void wrapDetails(OrderItemQualifier model, HttpServletRequest request) {
        this.offerId = model.getOffer().getId();
        this.quantity = model.getQuantity();
    }

    @Override
    public void wrapSummary(OrderItemQualifier model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

}
