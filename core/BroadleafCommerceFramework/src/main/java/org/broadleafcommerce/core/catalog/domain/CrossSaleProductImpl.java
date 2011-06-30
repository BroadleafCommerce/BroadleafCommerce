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

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.broadleafcommerce.core.catalog.domain.common.CrossSaleProductMappedSuperclass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(appliesTo="BLC_PRODUCT_CROSS_SALE", indexes={
		@Index(name="CROSSSALE_INDEX", columnNames={"PRODUCT_ID"}),
		@Index(name="CROSSSALE_RELATED_INDEX", columnNames={"RELATED_SALE_PRODUCT_ID"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class CrossSaleProductImpl extends CrossSaleProductMappedSuperclass implements RelatedProduct {

	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = ProductImpl.class, optional=false)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product = new ProductImpl();

    @ManyToOne(targetEntity = ProductImpl.class, optional=false)
    @JoinColumn(name = "RELATED_SALE_PRODUCT_ID", referencedColumnName = "PRODUCT_ID")
    private Product relatedSaleProduct = new ProductImpl();

    public Product getProduct() {
        return product;
    }

    public Product getRelatedProduct() {
        return relatedSaleProduct;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setRelatedProduct(Product relatedSaleProduct) {
        this.relatedSaleProduct = relatedSaleProduct;
    }
}
