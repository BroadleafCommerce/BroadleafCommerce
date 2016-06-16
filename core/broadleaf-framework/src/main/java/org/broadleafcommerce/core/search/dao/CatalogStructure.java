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

    public Map<String, BigDecimal> getDisplayOrdersByCategoryProduct() {
        return displayOrdersByCategoryProduct;
    }

    public void setDisplayOrdersByCategoryProduct(Map<String, BigDecimal> displayOrdersByCategoryProduct) {
        this.displayOrdersByCategoryProduct = displayOrdersByCategoryProduct;
    }

}
