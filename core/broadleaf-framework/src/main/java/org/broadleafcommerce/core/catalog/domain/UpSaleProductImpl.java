/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
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
@Table(name="BLC_PRODUCT_UP_SALE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = {"sandbox"}, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = {"multiTenantCatalog"})
})
public class UpSaleProductImpl implements RelatedProduct {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "UpSaleProductId")
    @GenericGenerator(
        name="UpSaleProductId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="UpSaleProductImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.UpSaleProductImpl")
        }
    )
    @Column(name = "UP_SALE_PRODUCT_ID")
    private Long id;
    
    @Column(name = "PROMOTION_MESSAGE")
    @AdminPresentation(friendlyName = "UpSaleProductImpl_Upsale_Promotion_Message", largeEntry=true)
    private String promotionMessage;

    @Column(name = "SEQUENCE")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    private Long sequence;
    
    @ManyToOne(targetEntity = ProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name="UPSALE_PRODUCT_INDEX", columnNames={"PRODUCT_ID"})
    private Product product;
    
    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    @Index(name="UPSALE_CATEGORY_INDEX", columnNames={"CATEGORY_ID"})
    protected Category category;

    @ManyToOne(targetEntity = ProductImpl.class)
    @JoinColumn(name = "RELATED_SALE_PRODUCT_ID", referencedColumnName = "PRODUCT_ID")
    @Index(name="UPSALE_RELATED_INDEX", columnNames={"RELATED_SALE_PRODUCT_ID"})
    private Product relatedSaleProduct = new ProductImpl();

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
    public Long getSequence() {
        return sequence;
    }
    
    @Override
    public void setSequence(Long sequence) {
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
}
