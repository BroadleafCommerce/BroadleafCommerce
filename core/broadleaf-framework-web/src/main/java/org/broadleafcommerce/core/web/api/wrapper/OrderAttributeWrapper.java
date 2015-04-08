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
    public void wrapDetails(OrderAttribute model, HttpServletRequest request) {
        this.id = model.getId();
        this.name = model.getName();
        this.value = model.getValue();
        this.orderId = model.getOrder().getId();
    }
    
    @Override
    public void wrapSummary(OrderAttribute model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    
    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    
    /**
     * @return the orderId
     */
    public Long getOrderId() {
        return orderId;
    }

    
    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
