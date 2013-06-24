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

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * API wrapper to wrap Order Item Price Detail Adjustments.
 * @author Priyesh Patel
 *
 */
@XmlRootElement(name = "orderItemPriceDetailAdjustment")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OrderItemPriceDetailAdjustmentWrapper extends BaseWrapper implements
        APIWrapper<OrderItemPriceDetailAdjustment> {
    
    @XmlElement
    protected Long id;
    
    @XmlElement
    protected String offerName;
    @XmlElement
    protected BigDecimal offerValue;

    @Override
    public void wrapDetails(OrderItemPriceDetailAdjustment model, HttpServletRequest request) {
        this.id = model.getId();
        this.offerName = model.getOfferName();
        this.offerValue = model.getValue().getAmount();
    }
    
    @Override
    public void wrapSummary(OrderItemPriceDetailAdjustment model, HttpServletRequest request) {
        wrapDetails(model, request);
    }
}
