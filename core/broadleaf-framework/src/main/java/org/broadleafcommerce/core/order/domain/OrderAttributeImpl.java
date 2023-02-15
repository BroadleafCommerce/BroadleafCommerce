/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
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
import javax.persistence.UniqueConstraint;

/**
 * The Class OrderAttributeImpl
 * @see OrderAttribute
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_ORDER_ATTRIBUTE",
                uniqueConstraints = @UniqueConstraint(name = "ATTR_NAME_ORDER_ID", columnNames = {"NAME", "ORDER_ID"}))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOrderElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
@AdminPresentationClass(friendlyName = "OrderAttributeImpl_baseProductAttribute")
public class OrderAttributeImpl implements OrderAttribute {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator= "OrderAttributeId")
    @GenericGenerator(
        name="OrderAttributeId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OrderAttributeImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.OrderAttributeImpl")
        }
    )
    @Column(name = "ORDER_ATTRIBUTE_ID")
    protected Long id;
    
    @Column(name = "NAME", nullable=false)
    @AdminPresentation(friendlyName = "OrderAttributeImpl_Attribute_Name", order=1000, prominent=true)
    protected String name;

    /** The value. */
    @Column(name = "VALUE")
    @AdminPresentation(friendlyName = "OrderAttributeImpl_Attribute_Value", order=2000, prominent=true)
    protected String value;
    
    @ManyToOne(targetEntity = OrderImpl.class, optional=false)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

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
    public Order getOrder() {
        return order;
    }

    @Override
    public void setOrder(Order order) {
        this.order = order;
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
        
        return value.equals(((OrderAttribute) obj).getValue());
    }

    @Override
    public <G extends OrderAttribute> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        OrderAttribute cloned = createResponse.getClone();
        cloned.setName(name);
        cloned.setValue(value);
        //dont clone
        cloned.setOrder(order);
        return createResponse;
    }
}
