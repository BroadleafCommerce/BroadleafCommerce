/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */
package org.broadleafcommerce.core.order.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
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
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_OPTION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(friendlyName = "Base Fulfillment Option")
public abstract class FulfillmentOptionImpl implements FulfillmentOption {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "FulfillmentOptionId")
    @GenericGenerator(
        name="FulfillmentOptionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="FulfillmentOptionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.domain.FulfillmentOptionImpl")
        }
    )
    @Column(name = "FULFILLMENT_OPTION_ID")
    protected Long id;
    
    @Column(name = "NAME")
    protected String name;

    @Lob
    @Column(name = "LONG_DESCRIPTION")
    protected String longDescription;

    @Column(name = "USE_FLAT_RATES")
    protected Boolean useFlatRates = true;

    @Column(name = "ADD_FULFILLMENT_FEES")
    protected Boolean addFulfillmentFees;

    @Column(name = "FULFILLMENT_TYPE", nullable = false)
    @AdminPresentation(friendlyName = "Fulfillment Type", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.order.service.type.FulfillmentType")
    protected String fulfillmentType = FulfillmentType.PHYSICAL.getType();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Override
    public Boolean getUseFlatRates() {
        return useFlatRates;
    }

    @Override
    public void setUseFlatRates(Boolean useFlatRates) {
        this.useFlatRates = useFlatRates;
    }
    
    @Override
    public Boolean getAddFulfillmentFees() {
        return addFulfillmentFees;
    }

    @Override
    public void setAddFulfillmentFees(Boolean addFulfillmentFees) {
        this.addFulfillmentFees = addFulfillmentFees;
    }
    
    @Override
    public FulfillmentType getFulfillmentType() {
        return FulfillmentType.getInstance(fulfillmentType);
    }

    @Override
    public void setFulfillmentType(FulfillmentType fulfillmentType) {
        this.fulfillmentType = (fulfillmentType == null) ? null : fulfillmentType.getType();
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
