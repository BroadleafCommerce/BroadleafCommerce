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
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_GIFTWRAP_ORDER_ITEM")
@AdminPresentationClass(friendlyName = "GiftWrapOrderItemImpl_giftWrapOrderItem")
public class GiftWrapOrderItemImpl extends DiscreteOrderItemImpl implements GiftWrapOrderItem {

    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "giftWrapOrderItem", targetEntity = OrderItemImpl.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderItemImpl_Price_Details",
                tab = OrderItemImpl.Presentation.Tab.Name.Advanced, tabOrder = OrderItemImpl.Presentation.Tab.Order.Advanced)
    protected List<OrderItem> wrappedItems = new ArrayList<OrderItem>();

    public List<OrderItem> getWrappedItems() {
        return wrappedItems;
    }

    public void setWrappedItems(List<OrderItem> wrappedItems) {
        this.wrappedItems = wrappedItems;
    }

    @Override
    public OrderItem clone() {
        GiftWrapOrderItem orderItem = (GiftWrapOrderItem) super.clone();
        if (wrappedItems != null) orderItem.getWrappedItems().addAll(wrappedItems);
        
        return orderItem;
    }

    @Override
    public CreateResponse<DiscreteOrderItem> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<DiscreteOrderItem> createResponse = super.createOrRetrieveCopyInstance(context);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        GiftWrapOrderItem cloned = (GiftWrapOrderItem)createResponse.getClone();
        for(OrderItem entry : wrappedItems){
            OrderItem clonedEntry = ((OrderItemImpl)entry).createOrRetrieveCopyInstance(context).getClone();
            clonedEntry.setGiftWrapOrderItem(cloned);
            cloned.getWrappedItems().add(clonedEntry);
        }
        return  createResponse;
    }

    @Override
    public int hashCode() {
        final int prime = super.hashCode();
        int result = super.hashCode();
        result = prime * result + ((wrappedItems == null) ? 0 : wrappedItems.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null) 
            return false;
        if (!super.equals(obj))
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        GiftWrapOrderItemImpl other = (GiftWrapOrderItemImpl) obj;

        if (!super.equals(obj)) {
            return false;
        }
        
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (wrappedItems == null) {
            if (other.wrappedItems != null)
                return false;
        } else if (!wrappedItems.equals(other.wrappedItems))
            return false;
        return true;
    }
}
