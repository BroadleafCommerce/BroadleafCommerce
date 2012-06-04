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

package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
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

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_OPTION_VALUE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(friendlyName = "Product Option Value")
public class ProductOptionValueImpl implements ProductOptionValue {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "ProductOptionValueId")
    @GenericGenerator(
        name="ProductOptionValueId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ProductOptionValueImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl")
        }
    )
    @Column(name = "PRODUCT_OPTION_VALUE_ID")
    protected Long id;

    @Column(name = "ATTRIBUTE_VALUE")
    @AdminPresentation(friendlyName = "Attribute Value")
    protected String attributeValue;

    @Column(name ="DISPLAY_ORDER")
    @AdminPresentation(friendlyName = "Display Order")
    protected Long displayOrder;

    @ManyToOne(targetEntity = ProductOptionImpl.class)
    @JoinColumn(name = "PRODUCT_OPTION_ID")
    protected ProductOption productOption;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAttributeValue() {
        return attributeValue;
    }

    @Override
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public ProductOption getProductOption() {
        return productOption;
    }

    @Override
    public void setProductOption(ProductOption productOption) {
        this.productOption = productOption;
    }

    @Override
    public Long getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }
}
