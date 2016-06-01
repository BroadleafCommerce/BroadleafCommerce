/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.dao;

import java.math.BigDecimal;

/**
 * Container object for the results of a lightweight query that retrieves the parent category
 * for a child product
 *
 * @author Jeff Fischer
 */
public class ParentCategoryByProduct {

    protected Long category;
    protected Long product;
    protected BigDecimal displayOrder;

    public ParentCategoryByProduct(Long category, Long product, BigDecimal displayOrder) {
        this.category = category;
        this.product = product;
        this.displayOrder = displayOrder;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    public BigDecimal getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(BigDecimal displayOrder) {
        this.displayOrder = displayOrder;
    }
    
}
