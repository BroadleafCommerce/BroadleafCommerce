/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import java.util.Objects;

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
@Polymorphism(type = PolymorphismType.EXPLICIT)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SKU_OPTION_VALUE_XREF")
@AdminPresentationClass(excludeFromPolymorphism = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class SkuProductOptionValueXrefImpl implements SkuProductOptionValueXref {

    private static final long serialVersionUID = 1L;
    
    public SkuProductOptionValueXrefImpl() { }

    public SkuProductOptionValueXrefImpl(Sku sku, ProductOptionValue val) {
        this.sku = sku;
        this.productOptionValue = val;
    }

    @Id
    @GeneratedValue(generator= "SkuProductOptionValueXrefId")
    @GenericGenerator(
        name="SkuProductOptionValueXrefId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SkuProductOptionValueXrefImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.SkuProductOptionValueXrefImpl")
        }
    )
    @Column(name = "SKU_OPTION_VALUE_XREF_ID")
    protected Long id;

    @ManyToOne(targetEntity = SkuImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "SKU_ID")
    protected Sku sku;

    @ManyToOne(targetEntity = ProductOptionValueImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "PRODUCT_OPTION_VALUE_ID")
    protected ProductOptionValue productOptionValue;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Sku getSku() {
        return sku;
    }
    
    @Override
    public void setSku(Sku sku) {
        this.sku = sku;
    }
    
    @Override
    public ProductOptionValue getProductOptionValue() {
        return productOptionValue;
    }
    
    @Override
    public void setProductOptionValue(ProductOptionValue productOptionValue) {
        this.productOptionValue = productOptionValue;
    }

    @Override
    public <G extends SkuProductOptionValueXref> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        SkuProductOptionValueXref cloned = createResponse.getClone();
        if (sku != null) {
            cloned.setSku(sku.createOrRetrieveCopyInstance(context).getClone());
        }
        if (productOptionValue != null) {
            cloned.setProductOptionValue(productOptionValue.createOrRetrieveCopyInstance(context).getClone());
        }
        return createResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkuProductOptionValueXrefImpl that = (SkuProductOptionValueXrefImpl) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(getSku(), that.getSku()) &&
                Objects.equals(getProductOptionValue(), that.getProductOptionValue());
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, getSku(), getProductOptionValue());
    }
}
