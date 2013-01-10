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
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_OPTION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(friendlyName = "Base Product Option", populateToOneFields=PopulateToOneFieldsEnum.TRUE)
public class ProductOptionImpl implements ProductOption {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "ProductOptionId")
    @GenericGenerator(
        name="ProductOptionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ProductOptionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.ProductOptionImpl")
        }
    )
    @Column(name = "PRODUCT_OPTION_ID")
    protected Long id;
    
    @Column(name = "OPTION_TYPE")
    @AdminPresentation(friendlyName = "Type", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.catalog.service.type.ProductOptionType")
    protected String type;
    
    @Column(name = "ATTRIBUTE_NAME")
    @AdminPresentation(friendlyName = "Attribute Name", tooltip="The attribute name that will appear in the order item attributes for this option")
    protected String attributeName;
    
    @Column(name = "LABEL")
    @AdminPresentation(friendlyName = "Label", tooltip="Text to display for the set of option values")
    protected String label;

    @Column(name = "REQUIRED")
    @AdminPresentation(friendlyName = "Required")
    protected Boolean required;
    
    @Column(name = "DISPLAY_ORDER")
    @AdminPresentation(friendlyName = "Display Order")
    protected Integer displayOrder;
    
    @OneToMany(mappedBy = "productOption", targetEntity = ProductOptionValueImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @OrderBy(value = "displayOrder")
    protected List<ProductOptionValue> allowedValues = new ArrayList<ProductOptionValue>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = ProductImpl.class)
    @JoinTable(name = "BLC_PRODUCT_OPTION_XREF", joinColumns = @JoinColumn(name = "PRODUCT_OPTION_ID", referencedColumnName = "PRODUCT_OPTION_ID"), inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<Product> products;
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public ProductOptionType getType() {
        return ProductOptionType.getInstance(type);
    }

    @Override
    public void setType(ProductOptionType type) {
        this.type = type == null ? null : type.getType();
    }
    
    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
    
    @Override
    public String getLabel() {
        return label;
    }
    
    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public Boolean getRequired() {
        return required;
    }

    @Override
    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public List<Product> getProducts() {
        return products;
    }

    @Override
    public void setProducts(List<Product> products){
        this.products = products;
    }

    @Override
    public List<ProductOptionValue> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public void setAllowedValues(List<ProductOptionValue> allowedValues) {
        this.allowedValues = allowedValues;
    }

}
