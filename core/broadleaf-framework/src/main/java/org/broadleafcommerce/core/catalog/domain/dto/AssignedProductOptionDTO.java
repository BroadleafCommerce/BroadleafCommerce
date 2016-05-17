/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.domain.dto;

import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;

/**
 * DTO used to carry back the found {@link org.broadleafcommerce.core.catalog.domain.ProductOptionValue#getId()} and
 * {@link org.broadleafcommerce.core.catalog.domain.ProductOption#getAttributeName()} on a given
 * {@link org.broadleafcommerce.core.catalog.domain.Product}
 *
 * @author Jerry Ocanas (jocanas)
 */
public class AssignedProductOptionDTO {

    private Long productId;
    private String productOptionAttrName;
    private ProductOptionValue productOptionValue;
    private Sku sku;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductOptionAttrName() {
        return productOptionAttrName;
    }

    public void setProductOptionAttrName(String productOptionAttrName) {
        this.productOptionAttrName = productOptionAttrName;
    }

    public ProductOptionValue getProductOptionValue() {
        return productOptionValue;
    }

    public void setProductOptionValue(ProductOptionValue productOptionValue) {
        this.productOptionValue = productOptionValue;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }
}
