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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lightweight bean representation of
 * <p>
 * (1) All the immediate parent categories for a given product
 * (2) All the immediate parent categories for a give category and
 * (3) All the child products for a given category
 * </p>
 *
 * @author Jeff Fischer
 */
public class CatalogStructure implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Map<Long, Set<Long>> parentCategoriesByProduct = new HashMap<Long, Set<Long>>();
    protected Map<Long, Set<Long>> parentCategoriesByCategory = new HashMap<Long, Set<Long>>();
    protected Map<Long, List<Long>> productsByCategory = new HashMap<Long, List<Long>>();
    protected Map<String, BigDecimal> displayOrdersByCategoryProduct = new HashMap<String, BigDecimal>();

    public Map<Long, Set<Long>> getParentCategoriesByProduct() {
        return parentCategoriesByProduct;
    }

    public void setParentCategoriesByProduct(Map<Long, Set<Long>> parentCategoriesByProduct) {
        this.parentCategoriesByProduct = parentCategoriesByProduct;
    }

    public Map<Long, Set<Long>> getParentCategoriesByCategory() {
        return parentCategoriesByCategory;
    }

    public void setParentCategoriesByCategory(Map<Long, Set<Long>> parentCategoriesByCategory) {
        this.parentCategoriesByCategory = parentCategoriesByCategory;
    }

    public Map<Long, List<Long>> getProductsByCategory() {
        return productsByCategory;
    }

    public void setProductsByCategory(Map<Long, List<Long>> productsByCategory) {
        this.productsByCategory = productsByCategory;
    }

    public Map<String, BigDecimal> getDisplayOrdersByCategoryProduct() {
        return displayOrdersByCategoryProduct;
    }

    public void setDisplayOrdersByCategoryProduct(Map<String, BigDecimal> displayOrdersByCategoryProduct) {
        this.displayOrdersByCategoryProduct = displayOrdersByCategoryProduct;
    }

}
