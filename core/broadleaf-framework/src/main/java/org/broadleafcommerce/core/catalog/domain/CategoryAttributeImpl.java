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

import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
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
 * 
 * @author Phillip Verheyden
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_CATEGORY_ATTRIBUTE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
@AdminPresentationClass(friendlyName = "baseCategoryAttribute")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class CategoryAttributeImpl implements CategoryAttribute {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator= "CategoryAttributeId")
    @GenericGenerator(
        name="CategoryAttributeId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="CategoryAttributeImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.CategoryAttributeImpl")
        }
    )
    @Column(name = "CATEGORY_ATTRIBUTE_ID")
    protected Long id;
    
    @Column(name = "NAME", nullable=false)
    @Index(name="CATEGORYATTRIBUTE_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String name;

    @Column(name = "VALUE")
    @AdminPresentation(friendlyName = "ProductAttributeImpl_Attribute_Value", translatable = true, order=2, group = "ProductAttributeImpl_Description", prominent=true)
    protected String value;

    @Column(name = "SEARCHABLE")
    @AdminPresentation(excluded = true)
    protected Boolean searchable = false;
    
    @ManyToOne(targetEntity = CategoryImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "CATEGORY_ID")
    @Index(name="CATEGORYATTRIBUTE_INDEX", columnNames={"CATEGORY_ID"})
    protected Category category;

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
        return DynamicTranslationProvider.getValue(this, "value", value);
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Boolean getSearchable() {
        if (searchable == null) {
            return Boolean.FALSE;
        } else {
            return searchable;
        }
    }

    @Override
    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
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
    public String toString() {
        return value;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
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
        CategoryAttributeImpl other = (CategoryAttributeImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public <G extends CategoryAttribute> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        CategoryAttribute cloned = createResponse.getClone();
        if (category != null) {
            cloned.setCategory(category.createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setName(name);
        cloned.setValue(value);
        cloned.setSearchable(searchable);
        return  createResponse;
    }
}
