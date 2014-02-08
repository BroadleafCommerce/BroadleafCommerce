/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_MULTISHIP_OPTION")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class OrderMultishipOptionImpl implements OrderMultishipOption {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderMultishipOptionId")
    @GenericGenerator(
        name="OrderMultishipOptionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OrderMultishipOptionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.domain.OrderMultishipOptionImpl")
        }
    )
    @Column(name = "ORDER_MULTISHIP_OPTION_ID")
    protected Long id;
    
    @ManyToOne(targetEntity = OrderImpl.class)
    @JoinColumn(name = "ORDER_ID")
    @Index(name="MULTISHIP_OPTION_ORDER_INDEX", columnNames={"ORDER_ID"})
    protected Order order;

    @ManyToOne(targetEntity = OrderItemImpl.class)
    @JoinColumn(name = "ORDER_ITEM_ID")
    protected OrderItem orderItem;

    @ManyToOne(targetEntity = AddressImpl.class)
    @JoinColumn(name = "ADDRESS_ID")
    protected Address address;
    
    @ManyToOne(targetEntity = FulfillmentOptionImpl.class)
    @JoinColumn(name = "FULFILLMENT_OPTION_ID")
    protected FulfillmentOption fulfillmentOption;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Order getOrder() {
        return order;
    }

    @Override
    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public OrderItem getOrderItem() {
        return orderItem;
    }

    @Override
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public FulfillmentOption getFulfillmentOption() {
        return fulfillmentOption;
    }

    @Override
    public void setFulfillmentOption(FulfillmentOption fulfillmentOption) {
        this.fulfillmentOption = fulfillmentOption;
    }

}
