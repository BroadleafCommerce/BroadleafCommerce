/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
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
    
    @Override
    public CreateResponse<OrderMultishipOption> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<OrderMultishipOption> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        OrderMultishipOption cloned = createResponse.getClone();
        cloned.setAddress(address.createOrRetrieveCopyInstance(context).getClone());
        cloned.setFulfillmentOption(fulfillmentOption.createOrRetrieveCopyInstance(context).getClone());
        cloned.setOrder(order);
        cloned.setOrderItem(orderItem.createOrRetrieveCopyInstance(context).getClone());
        return  createResponse;
    }
}
