/*
 * Copyright 2008-2009 the original author or authors.
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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_FEATURED")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@XmlRootElement(name = "featuredProduct")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class FeaturedProductImpl implements FeaturedProduct {

	private static final long serialVersionUID = 1L;

	/** The id. */
    @Id
    @GeneratedValue(generator= "FeaturedProductId")
    @GenericGenerator(
        name="FeaturedProductId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="FeaturedProductImpl"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.FeaturedProductImpl")
        }
    )
    @Column(name = "FEATURED_PRODUCT_ID")
    protected Long id;
    
    @Column(name = "SEQUENCE")
    protected Long sequence;

    @Column(name = "PROMOTION_MESSAGE")
    @AdminPresentation(friendlyName="Featured Product Promotion Message", largeEntry=true)
    protected String promotionMessage;
    
	@ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    @Index(name="PRODFEATURED_CATEGORY_INDEX", columnNames={"CATEGORY_ID"})
    protected Category category = new CategoryImpl();

    @ManyToOne(targetEntity = ProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name="PRODFEATURED_PRODUCT_INDEX", columnNames={"PRODUCT_ID"})
    protected Product product = new ProductImpl();

    @XmlElement
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    @XmlElement
    public Long getSequence() {
    	return this.sequence;
    }

    @XmlElement
    public String getPromotionMessage() {
        return promotionMessage;
    }

    public void setPromotionMessage(String promotionMessage) {
        this.promotionMessage = promotionMessage;
    }

    @XmlElement
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @XmlElement
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    
}