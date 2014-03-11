package org.broadleafcommerce.core.search.dao;

import java.io.Serializable;
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

    Map<Long, Set<Long>> parentCategoriesByProduct = new HashMap<Long, Set<Long>>();
    Map<Long, Set<Long>> parentCategoriesByCategory = new HashMap<Long, Set<Long>>();
    Map<Long, List<Long>> productsByCategory = new HashMap<Long, List<Long>>();

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

}
