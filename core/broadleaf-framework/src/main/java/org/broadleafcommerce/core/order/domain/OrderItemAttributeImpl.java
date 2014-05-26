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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.lang.reflect.Method;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Arbitrary attributes to add to an order-item.
 *
 * @see org.broadleafcommerce.core.order.domain.OrderItemAttribute
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_ORDER_ITEM_ATTRIBUTE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOrderElements")
@AdminPresentationClass(friendlyName = "OrderItemAttributeImpl_baseProductAttribute")
public class OrderItemAttributeImpl implements OrderItemAttribute {

    public static final Log LOG = LogFactory.getLog(OrderItemAttributeImpl.class);
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator= "OrderItemAttributeId")
    @GenericGenerator(
        name="OrderItemAttributeId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OrderItemAttributeImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.OrderItemAttributeImpl")
        }
    )
    @Column(name = "ORDER_ITEM_ATTRIBUTE_ID")
    protected Long id;
    
    @Column(name = "NAME", nullable=false)
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String name;

    @Column(name = "VALUE", nullable=false)
    @AdminPresentation(friendlyName = "OrderItemAttributeImpl_Attribute_Value", order=2, group = "OrderItemAttributeImpl_Description", prominent=true)
    protected String value;
    
    @ManyToOne(targetEntity = OrderItemImpl.class, optional=false)
    @JoinColumn(name = "ORDER_ITEM_ID")
    protected OrderItem orderItem;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public OrderItem getOrderItem() {
        return orderItem;
    }

    @Override
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public void checkCloneable(OrderItemAttribute itemAttribute) throws CloneNotSupportedException, SecurityException, NoSuchMethodException {
        Method cloneMethod = itemAttribute.getClass().getMethod("clone", new Class[]{});
        if (cloneMethod.getDeclaringClass().getName().startsWith("org.broadleafcommerce") && !itemAttribute.getClass().getName().startsWith("org.broadleafcommerce")) {
            //subclass is not implementing the clone method
            throw new CloneNotSupportedException("Custom extensions and implementations should implement clone in order to guarantee split and merge operations are performed accurately");
        }
    }

    @Override
    public OrderItemAttribute clone() {
        //instantiate from the fully qualified name via reflection
        OrderItemAttribute itemAttribute;
        try {
            itemAttribute = (OrderItemAttribute) Class.forName(this.getClass().getName()).newInstance();
            try {
                checkCloneable(itemAttribute);
            } catch (CloneNotSupportedException e) {
                LOG.warn("Clone implementation missing in inheritance hierarchy outside of Broadleaf: " + itemAttribute.getClass().getName(), e);
            }            
            itemAttribute.setName(name);
            itemAttribute.setOrderItem(orderItem);
            itemAttribute.setValue(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return itemAttribute;
    }


    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        
        if (value == null) {
            return false;
        }
        
        return value.equals(((OrderItemAttribute) obj).getValue());
    }
}
