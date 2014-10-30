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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
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
 * The Class ProductAttributeImpl.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_PRODUCT_ATTRIBUTE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
@AdminPresentationClass(friendlyName = "ProductAttributeImpl_baseProductAttribute")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG, skipOverlaps=true)
})
public class ProductAttributeImpl implements ProductAttribute {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The id. */
    @Id
    @GeneratedValue(generator= "ProductAttributeId")
    @GenericGenerator(
        name="ProductAttributeId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ProductAttributeImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.ProductAttributeImpl")
        }
    )
    @Column(name = "PRODUCT_ATTRIBUTE_ID")
    protected Long id;
    
    /** The name. */
    @Column(name = "NAME", nullable=false)
    @Index(name="PRODUCTATTRIBUTE_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String name;

    /** The value. */
    @Column(name = "VALUE")
    @AdminPresentation(friendlyName = "ProductAttributeImpl_Attribute_Value", order=2, group = "ProductAttributeImpl_Description", prominent=true)
    protected String value;

    /** The searchable. */
    @Column(name = "SEARCHABLE")
    @AdminPresentation(excluded = true)
    protected Boolean searchable = false;
    
    /** The product. */
    @ManyToOne(targetEntity = ProductImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name="PRODUCTATTRIBUTE_INDEX", columnNames={"PRODUCT_ID"})
    protected Product product;
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#getValue()
     */
    @Override
    public String getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#setValue(java.lang.String)
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#getSearchable()
     */
    @Override
    public Boolean getSearchable() {
        if (searchable == null) {
            return Boolean.FALSE;
        } else {
            return searchable;
        }
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#setSearchable(java.lang.Boolean)
     */
    @Override
    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return value;
    }
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#getProduct()
     */
    @Override
    public Product getProduct() {
        return product;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#setProduct(org.broadleafcommerce.core.catalog.domain.Product)
     */
    @Override
    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((product == null) ? 0 : product.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        ProductAttributeImpl other = (ProductAttributeImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public <G extends ProductAttribute> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        ProductAttribute cloned = createResponse.getClone();
        // dont clone
        cloned.setProduct(product);
        cloned.setName(name);
        cloned.setSearchable(searchable);
        cloned.setValue(value);
        return  createResponse;
    }
}
