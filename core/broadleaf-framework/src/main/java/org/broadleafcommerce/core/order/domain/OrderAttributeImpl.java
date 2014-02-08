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

/**
 * The Class OrderAttributeImpl
 * @see OrderAttribute
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_ORDER_ATTRIBUTE")
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
        if (getClass() != obj.getClass())
            return false;
        
        if (value == null) {
            return false;
        }
        
        return value.equals(((OrderAttribute) obj).getValue());
    }
}
