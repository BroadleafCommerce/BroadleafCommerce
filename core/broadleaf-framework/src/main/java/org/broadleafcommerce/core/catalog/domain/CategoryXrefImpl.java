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
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;

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

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY_XREF")
@AdminPresentationClass(excludeFromPolymorphism = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blCategoryRelationships")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class CategoryXrefImpl implements CategoryXref {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "CategoryXrefId")
    @GenericGenerator(
        name="CategoryXrefId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="CategoryXrefImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl")
        }
    )
    @Column(name = "CATEGORY_XREF_ID")
    protected Long id;

    @ManyToOne(targetEntity = CategoryImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "CATEGORY_ID")
    protected Category category = new CategoryImpl();

    @ManyToOne(targetEntity = CategoryImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "SUB_CATEGORY_ID")
    protected Category subCategory = new CategoryImpl();

    @Column(name = "DISPLAY_ORDER", precision = 10, scale = 6)
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected BigDecimal displayOrder;

    @Column(name = "DEFAULT_REFERENCE")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected Boolean defaultReference;

    @Override
    public BigDecimal getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(final BigDecimal displayOrder) {
        this.displayOrder = displayOrder;
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
    public Category getSubCategory() {
        return subCategory;
    }

    @Override
    public void setSubCategory(Category subCategory) {
        this.subCategory = subCategory;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Boolean getDefaultReference() {
        return defaultReference;
    }

    @Override
    public void setDefaultReference(Boolean defaultReference) {
        this.defaultReference = defaultReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        CategoryXrefImpl that = (CategoryXrefImpl) o;

        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (subCategory != null ? !subCategory.equals(that.subCategory) : that.subCategory != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (subCategory != null ? subCategory.hashCode() : 0);
        return result;
    }

    @Override
    public <G extends CategoryXref> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        CategoryXref cloned = createResponse.getClone();
        if (category != null) {
            cloned.setCategory(category.createOrRetrieveCopyInstance(context).getClone());
        }
        if (subCategory != null) {
            cloned.setSubCategory(subCategory.createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setDisplayOrder(displayOrder);
        cloned.setDefaultReference(defaultReference);
        return createResponse;
    }
}
