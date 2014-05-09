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

import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.util.BLCCollectionUtils;
import org.broadleafcommerce.common.util.TypedTransformer;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.springframework.stereotype.Repository;

import com.google.common.collect.BiMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
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

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Override
    public void populateProductCatalogStructure(List<Long> productIds, CatalogStructure catalogStructure) {
        Map<Long, Set<Long>> parentCategoriesByProduct = new HashMap<Long, Set<Long>>();
        Map<Long, Set<Long>> parentCategoriesByCategory = new HashMap<Long, Set<Long>>();

        Long[] products = productIds.toArray(new Long[productIds.size()]);
        BiMap<Long, Long> sandBoxProductToOriginalMap = sandBoxHelper.getSandBoxToOriginalMap(em, ProductImpl.class, products);
        int batchSize = 800;
        int count = 0;
        int pos = 0;
        while (pos < products.length) {
            int remaining = products.length - pos;
            int mySize = remaining > batchSize ? batchSize : remaining;
            Long[] temp = new Long[mySize];
            System.arraycopy(products, pos, temp, 0, mySize);
            TypedQuery<ParentCategoryByProduct> query = em.createNamedQuery("BC_READ_PARENT_CATEGORY_IDS_BY_PRODUCTS", ParentCategoryByProduct.class);
            query.setParameter("productIds", sandBoxHelper.mergeCloneIds(em, ProductImpl.class, temp));

            List<ParentCategoryByProduct> results = query.getResultList();
            for (ParentCategoryByProduct item : results) {
                Long sandBoxProductVal = item.getProduct();
                BiMap<Long, Long> reverse = sandBoxProductToOriginalMap.inverse();
                if (reverse.containsKey(sandBoxProductVal)) {
                    sandBoxProductVal = reverse.get(sandBoxProductVal);
                }
                if (!catalogStructure.getParentCategoriesByProduct().containsKey(sandBoxProductVal)) {
                    if (!parentCategoriesByProduct.containsKey(sandBoxProductVal)) {
                        parentCategoriesByProduct.put(sandBoxProductVal, new HashSet<Long>());
                    }
                    //We only want the sandbox parent - if applicable
                    Long sandBoxVal = sandBoxHelper.getSandBoxVersionId(em, CategoryImpl.class, item.getParent());
                    if (sandBoxVal == null) {
                        sandBoxVal = item.getParent();
                    }
                    parentCategoriesByProduct.get(sandBoxProductVal).add(sandBoxVal);
                }
            }
            for (Map.Entry<Long, Set<Long>> entry : parentCategoriesByProduct.entrySet()) {
                for (Long categoryId : entry.getValue()) {
                    if (!catalogStructure.getParentCategoriesByCategory().containsKey(categoryId)) {
                        Set<Long> hierarchy = new HashSet<Long>();
                        parentCategoriesByCategory.put(categoryId, hierarchy);
                    }
                    if (!catalogStructure.getProductsByCategory().containsKey(categoryId)) {
                        List<ProductsByCategoryWithOrder> categoryChildren = readProductIdsByCategory(categoryId);

                        // Cache the display order bigdecimals
                        for (ProductsByCategoryWithOrder child : categoryChildren) {
                            catalogStructure.getDisplayOrdersByCategoryProduct().put(categoryId + "-" + child.getProductId(), child.getDisplayOrder());
                        }

                        //filter the list for sandbox values
                        for (Map.Entry<Long, Long> sandBoxProduct : sandBoxProductToOriginalMap.entrySet()) {
                            for (ProductsByCategoryWithOrder child : categoryChildren) {
                                if (child.getProductId().equals(sandBoxProduct.getValue())) {
                                    child.setProductId(sandBoxProduct.getKey());
                                }
                            }
                        }
                        
                        List<Long> categoryChildProductIds = BLCCollectionUtils.collectList(categoryChildren, new TypedTransformer<Long>() {
                            @Override
                            public Long transform(Object input) {
                                return ((ProductsByCategoryWithOrder) input).getProductId();
                            }
                        });
                        catalogStructure.getProductsByCategory().put(categoryId, categoryChildProductIds);
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

    protected List<ProductsByCategoryWithOrder> readProductIdsByCategory(Long categoryId) {
        TypedQuery<ProductsByCategoryWithOrder> query = em.createNamedQuery("BC_READ_PRODUCT_IDS_BY_CATEGORY_WITH_ORDER", ProductsByCategoryWithOrder.class);
        query.setParameter("categoryIds", sandBoxHelper.mergeCloneIds(em, CategoryImpl.class, categoryId));
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
            query.setParameter("categoryIds", sandBoxHelper.mergeCloneIds(em, CategoryImpl.class, temp));
            List<ParentCategoryByCategory> results = query.getResultList();
            for (ParentCategoryByCategory item : results) {
                //only the sandbox child
                Long childSandBoxVal = sandBoxHelper.getSandBoxVersionId(em, CategoryImpl.class, item.getChild());
                if (childSandBoxVal == null) {
                    childSandBoxVal = item.getChild();
                }
                Set<Long> hierarchy = categoryHierarchy.get(childSandBoxVal);
                if (item.getParent() != null) {
                    //We only want the sandbox parent - if applicable
                    Long sandBoxVal = sandBoxHelper.getSandBoxVersionId(em, CategoryImpl.class, item.getParent());
                    if (sandBoxVal == null) {
                        sandBoxVal = item.getParent();
                    }
                    hierarchy.add(sandBoxVal);
                    if (!nextLevel.containsKey(sandBoxVal)) {
                        nextLevel.put(sandBoxVal, new HashSet<Long>());
                    }
                }
                if (item.getDefaultParent() != null) {
                    //We only want the sandbox parent - if applicable
                    Long sandBoxVal = sandBoxHelper.getSandBoxVersionId(em, CategoryImpl.class, item.getDefaultParent());
                    if (sandBoxVal == null) {
                        sandBoxVal = item.getDefaultParent();
                    }
                    hierarchy.add(sandBoxVal);
                    if (!nextLevel.containsKey(sandBoxVal)) {
                        nextLevel.put(sandBoxVal, new HashSet<Long>());
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
