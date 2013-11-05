/*
 * Copyright 2008-2013 the original author or authors.
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_OPTION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
@AdminPresentationClass(friendlyName = "Base Fulfillment Option")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class FulfillmentOptionImpl implements FulfillmentOption {

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
    @AdminPresentation(friendlyName = "FulfillmentOptionImpl_name",
            order = Presentation.FieldOrder.NAME, prominent = true, gridOrder = 1000, translatable = true)
    protected String name;

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "LONG_DESCRIPTION", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(friendlyName = "FulfillmentOptionImpl_longDescription",
            order = Presentation.FieldOrder.DESCRIPTION, translatable = true)
    protected String longDescription;

    @Column(name = "USE_FLAT_RATES")
    @AdminPresentation(friendlyName = "FulfillmentOptionImpl_useFlatRates",
        order = Presentation.FieldOrder.FLATRATES)
    protected Boolean useFlatRates = true;

    @Column(name = "FULFILLMENT_TYPE", nullable = false)
    protected String fulfillmentType;

    @Column(name = "TAX_CODE", nullable = true)
    protected String taxCode;

    @Column(name = "TAXABLE")
    protected Boolean taxable = false;

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
        return DynamicTranslationProvider.getValue(this, "name", name);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLongDescription() {
        return DynamicTranslationProvider.getValue(this, "longDescription", longDescription);
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

    @Override
    public Boolean getTaxable() {
        return this.taxable;
    }

    @Override
    public void setTaxable(Boolean taxable) {
        this.taxable = taxable;
    }

    @Override
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    @Override
    public String getTaxCode() {
        return this.taxCode;
    }

    public static class Presentation {
        public static class Group {
            public static class Name {
            }

            public static class Order {
            }
        }

        public static class FieldOrder {
            public static final int NAME = 1000;
            public static final int DESCRIPTION = 2000;
            public static final int FLATRATES = 9000;
        }
    }
}
