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
import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_PRODUCT_CROSS_SALE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class CrossSaleProductImpl implements RelatedProduct, MultiTenantCloneable<CrossSaleProductImpl> {

    protected static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "CrossSaleProductId")
    @GenericGenerator(
        name="CrossSaleProductId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="CrossSaleProductImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.CrossSaleProductImpl")
        }
    )
    @Column(name = "CROSS_SALE_PRODUCT_ID")
    protected Long id;
    
    @Column(name = "PROMOTION_MESSAGE")
    @AdminPresentation(friendlyName = "CrossSaleProductImpl_Cross_Sale_Promotion_Message", largeEntry=true)
    protected String promotionMessage;

    @Column(name = "SEQUENCE", precision = 10, scale = 6)
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected BigDecimal sequence;
    
    @ManyToOne(targetEntity = ProductImpl.class, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name="CROSSSALE_INDEX", columnNames={"PRODUCT_ID"})
    protected Product product;
    
    @ManyToOne(targetEntity = CategoryImpl.class, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "CATEGORY_ID")
    @Index(name="CROSSSALE_CATEGORY_INDEX", columnNames={"CATEGORY_ID"})
    protected Category category;

    @ManyToOne(targetEntity = ProductImpl.class, optional=false)
    @JoinColumn(name = "RELATED_SALE_PRODUCT_ID", referencedColumnName = "PRODUCT_ID")
    @Index(name="CROSSSALE_RELATED_INDEX", columnNames={"RELATED_SALE_PRODUCT_ID"})
    protected Product relatedSaleProduct = new ProductImpl();

    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getPromotionMessage() {
        return promotionMessage;
    }
    
    @Override
    public void setPromotionMessage(String promotionMessage) {
        this.promotionMessage = promotionMessage;
    }

    @Override
    public BigDecimal getSequence() {
        return sequence;
    }
    
    @Override
    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
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
    public Category getCategory() {
        return category;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public Product getRelatedProduct() {
        return relatedSaleProduct;
    }

    @Override
    public void setRelatedProduct(Product relatedSaleProduct) {
        this.relatedSaleProduct = relatedSaleProduct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        CrossSaleProductImpl that = (CrossSaleProductImpl) o;

        if (sequence != null ? !sequence.equals(that.sequence) : that.sequence != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (product != null ? !product.equals(that.product) : that.product != null) return false;
        if (relatedSaleProduct != null ? !relatedSaleProduct.equals(that.relatedSaleProduct) : that.relatedSaleProduct != null) return false;
        if (promotionMessage != null ? !promotionMessage.equals(that.promotionMessage) : that.promotionMessage != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (relatedSaleProduct != null ? relatedSaleProduct.hashCode() : 0);
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        result = 31 * result + (promotionMessage != null ? promotionMessage.hashCode() : 0);
        return result;
    }

    @Override
    public <G extends CrossSaleProductImpl> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        CrossSaleProductImpl cloned = createResponse.getClone();
        if (product != null) {
            cloned.setProduct(product.createOrRetrieveCopyInstance(context).getClone());
        }
        if (category != null) {
            cloned.setCategory(category.createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setPromotionMessage(promotionMessage);
        cloned.setSequence(sequence);
        if (relatedSaleProduct != null) {
            cloned.setRelatedProduct(relatedSaleProduct.createOrRetrieveCopyInstance(context).getClone());
        }
        return createResponse;
    }
}
