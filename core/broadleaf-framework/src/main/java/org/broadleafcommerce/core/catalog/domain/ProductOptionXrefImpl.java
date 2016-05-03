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
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_OPTION_XREF")
@AdminPresentationClass(excludeFromPolymorphism = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class ProductOptionXrefImpl implements ProductOptionXref {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "ProductOptionXrefId")
    @GenericGenerator(
        name="ProductOptionXrefId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ProductOptionXrefImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.ProductOptionXrefImpl")
        }
    )
    @Column(name = "PRODUCT_OPTION_XREF_ID")
    protected Long id;

    @ManyToOne(targetEntity = ProductImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product = new ProductImpl();

    @ManyToOne(targetEntity = ProductOptionImpl.class, optional=false)
    @JoinColumn(name = "PRODUCT_OPTION_ID")
    protected ProductOption productOption = new ProductOptionImpl();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Product getProduct() {
        return product;
    }

    @Override
    public void setProduct(Product product) {
        this.product = product;
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
    public <G extends ProductOptionXref> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        ProductOptionXref cloned = createResponse.getClone();
        if (product != null) {
            cloned.setProduct(product.createOrRetrieveCopyInstance(context).getClone());
        }
        if (productOption != null) {
            cloned.setProductOption(productOption.createOrRetrieveCopyInstance(context).getClone());
        }
        return createResponse;
    }
}
