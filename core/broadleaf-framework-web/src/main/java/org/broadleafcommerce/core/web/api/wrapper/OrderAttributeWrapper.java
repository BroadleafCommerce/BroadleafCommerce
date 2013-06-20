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

import org.broadleafcommerce.core.order.domain.OrderAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * API wrapper to wrap Order  Attributes.
 * @author Priyesh Patel
 *
 */
@XmlRootElement(name = "orderAttribute")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OrderAttributeWrapper extends BaseWrapper implements
        APIWrapper<OrderAttribute> {
    
    @XmlElement
    protected Long id;
    
    @XmlElement
    protected String name;
    
    @XmlElement
    protected String value;
    
    @XmlElement
    protected Long orderId;

    @Override
    public void wrap(OrderAttribute model, HttpServletRequest request) {
        this.id = model.getId();
        this.name = model.getName();
        this.value = model.getValue();
        this.orderId = model.getOrder().getId();
    }
    
}
