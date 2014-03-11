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

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * @see org.broadleafcommerce.core.search.dao.SolrIndexDao
 * @author Jeff Fischer
 */
@Repository("blSolrIndexDao")
public class SolrIndexDaoImpl implements SolrIndexDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Override
    public void populateCatalogStructure(List<Long> productIds, CatalogStructure catalogStructure) {
        Map<Long, Set<Long>> parentCategoriesByProduct = new HashMap<Long, Set<Long>>();
        Map<Long, Set<Long>> parentCategoriesByCategory = new HashMap<Long, Set<Long>>();

        Long[] products = productIds.toArray(new Long[productIds.size()]);
        int batchSize = 800;
        int count = 0;
        int pos = 0;
        while (pos < products.length) {
            int remaining = products.length - pos;
            int mySize = remaining > batchSize ? batchSize : remaining;
            Long[] temp = new Long[mySize];
            System.arraycopy(products, pos, temp, 0, mySize);
            TypedQuery<ParentCategoryByProduct> query = em.createNamedQuery("BC_READ_PARENT_CATEGORY_IDS_BY_PRODUCTS", ParentCategoryByProduct.class);
            query.setParameter("productIds", Arrays.asList(temp));
            List<ParentCategoryByProduct> results = query.getResultList();
            for (ParentCategoryByProduct item : results) {
                if (!catalogStructure.getParentCategoriesByProduct().containsKey(item.getProduct())) {
                    if (!parentCategoriesByProduct.containsKey(item.getProduct())) {
                        parentCategoriesByProduct.put(item.getProduct(), new HashSet<Long>());
                    }
                    parentCategoriesByProduct.get(item.getProduct()).add(item.getParent());
                }
            }
            for (Map.Entry<Long, Set<Long>> entry : parentCategoriesByProduct.entrySet()) {
                for (Long categoryId : entry.getValue()) {
                    if (!catalogStructure.getParentCategoriesByCategory().containsKey(categoryId)) {
                        Set<Long> hierarchy = new HashSet<Long>();
                        parentCategoriesByCategory.put(categoryId, hierarchy);
                    }
                    if (!catalogStructure.getProductsByCategory().containsKey(categoryId)) {
                        catalogStructure.getProductsByCategory().put(categoryId, readProductIdsByCategory(categoryId));
                    }
                }
            }
            count++;
            pos = (count * batchSize) < products.length ? (count * batchSize) : products.length;
        }
        readFullCategoryHierarchy(parentCategoriesByCategory);
        catalogStructure.getParentCategoriesByProduct().putAll(parentCategoriesByProduct);
        catalogStructure.getParentCategoriesByCategory().putAll(parentCategoriesByCategory);
    }

    protected List<Long> readProductIdsByCategory(Long categoryId) {
        TypedQuery<Long> query = em.createNamedQuery("BC_READ_PRODUCT_IDS_BY_CATEGORY", Long.class);
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }

    /**
     * Build up a map of category to parent categories
     *
     * @param categoryHierarchy
     */
    protected void readFullCategoryHierarchy(Map<Long, Set<Long>> categoryHierarchy) {
        Map<Long, Set<Long>> nextLevel = new HashMap<Long, Set<Long>>();
        Long[] categoryIds = categoryHierarchy.keySet().toArray(new Long[categoryHierarchy.keySet().size()]);
        int batchSize = 800;
        int count = 0;
        int pos = 0;
        while (pos < categoryIds.length) {
            int remaining = categoryIds.length - pos;
            int mySize = remaining > batchSize ? batchSize : remaining;
            Long[] temp = new Long[mySize];
            System.arraycopy(categoryIds, pos, temp, 0, mySize);
            TypedQuery<ParentCategoryByCategory> query = em.createNamedQuery("BC_READ_PARENT_CATEGORY_IDS_BY_CATEGORIES", ParentCategoryByCategory.class);
            query.setParameter("categoryIds", Arrays.asList(temp));
            List<ParentCategoryByCategory> results = query.getResultList();
            for (ParentCategoryByCategory item : results) {
                Set<Long> hierarchy = categoryHierarchy.get(item.getChild());
                if (item.getParent() != null) {
                    hierarchy.add(item.getParent());
                    if (!nextLevel.containsKey(item.getParent())) {
                        nextLevel.put(item.getParent(), new HashSet<Long>());
                    }
                }
                if (item.getDefaultParent() != null) {
                    hierarchy.add(item.getDefaultParent());
                    if (!nextLevel.containsKey(item.getDefaultParent())) {
                        nextLevel.put(item.getDefaultParent(), new HashSet<Long>());
                    }
                }
            }
            count++;
            pos = (count * batchSize) < categoryIds.length ? (count * batchSize) : categoryIds.length;
        }
        if (!nextLevel.isEmpty()) {
            readFullCategoryHierarchy(nextLevel);
        }
        categoryHierarchy.putAll(nextLevel);
    }

}
