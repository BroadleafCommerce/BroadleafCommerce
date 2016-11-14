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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.springframework.stereotype.Repository;

import com.google.common.collect.BiMap;

import java.math.BigDecimal;
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
    protected static final Log LOG = LogFactory.getLog(SolrIndexDaoImpl.class);

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;
    
    @Override
    public void populateProductCatalogStructure(List<Long> productIds, CatalogStructure catalogStructure) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        Boolean oldIgnoreFilters = context.getInternalIgnoreFilters();
        context.setInternalIgnoreFilters(false);
        try {
            Map<Long, Set<Long>> parentCategoriesByProduct = new HashMap<>();
            Map<Long, Set<Long>> parentCategoriesByCategory = new HashMap<>();
    
            Long[] products = productIds.toArray(new Long[productIds.size()]);
            BiMap<Long, Long> sandBoxProductToOriginalMap = sandBoxHelper.getSandBoxToOriginalMap(ProductImpl.class, products);
            int batchSize = 800;
            int count = 0;
            int pos = 0;
            while (pos < products.length) {
                int remaining = products.length - pos;
                int mySize = remaining > batchSize ? batchSize : remaining;
                Long[] temp = new Long[mySize];
                System.arraycopy(products, pos, temp, 0, mySize);
                
                //context.getAdditionalProperties().put("constrainedFilterGroups", Arrays.asList("archivedFilter"));
                TypedQuery<ParentCategoryByProduct> query = em.createNamedQuery("BC_READ_PARENT_CATEGORY_IDS_BY_PRODUCTS", ParentCategoryByProduct.class);
                query.setParameter("productIds", sandBoxHelper.mergeCloneIds(ProductImpl.class, temp));
    
                List<ParentCategoryByProduct> results = query.getResultList();
                //context.getAdditionalProperties().remove("constrainedFilterGroups");
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
                        //Long sandBoxVal = sandBoxHelper.getCombinedSandBoxVersionId(CategoryImpl.class, item.getParent());
                        Long sandBoxVal = sandBoxHelper.getSandBoxVersionId(CategoryImpl.class, item.getCategory());
                        if (sandBoxVal == null) {
                            sandBoxVal = item.getCategory();
                        }
                        parentCategoriesByProduct.get(sandBoxProductVal).add(sandBoxVal);
                    }

                    // Cache the display order bigdecimals
                    BigDecimal displayOrder = (item.getDisplayOrder() == null) ? new BigDecimal("1.00000") : item.getDisplayOrder();
                    catalogStructure.getDisplayOrdersByCategoryProduct().put(item.getCategory() + "-" + item.getProduct(), displayOrder);
                }
                for (Map.Entry<Long, Set<Long>> entry : parentCategoriesByProduct.entrySet()) {
                    for (Long categoryId : entry.getValue()) {
                        if (!catalogStructure.getParentCategoriesByCategory().containsKey(categoryId)) {
                            Set<Long> hierarchy = new HashSet<>();
                            parentCategoriesByCategory.put(categoryId, hierarchy);
                        }
                    }
                }
                count++;
                pos = (count * batchSize) < products.length ? (count * batchSize) : products.length;
            }
            readFullCategoryHierarchy(parentCategoriesByCategory, new HashSet<Long>());
            catalogStructure.getParentCategoriesByProduct().putAll(parentCategoriesByProduct);
            catalogStructure.getParentCategoriesByCategory().putAll(parentCategoriesByCategory);
        } finally {
            context.setInternalIgnoreFilters(oldIgnoreFilters);
        }
    }

    /**
     * Build up a map of category to parent categories
     *
     * @param categoryHierarchy
     */
    protected void readFullCategoryHierarchy(Map<Long, Set<Long>> categoryHierarchy, Set<Long> builtCategories) {
        Map<Long, Set<Long>> nextLevel = new HashMap<>();
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
            query.setParameter("categoryIds", sandBoxHelper.mergeCloneIds(CategoryImpl.class, temp));
            List<ParentCategoryByCategory> results = query.getResultList();
            for (ParentCategoryByCategory item : results) {
                //only the sandbox child
                //Long childSandBoxVal = sandBoxHelper.getCombinedSandBoxVersionId(CategoryImpl.class, item.getChild());
                Long childSandBoxVal = sandBoxHelper.getSandBoxVersionId(CategoryImpl.class, item.getChild());
                if (childSandBoxVal == null) {
                    childSandBoxVal = item.getChild();
                }
                
                if (builtCategories.contains(childSandBoxVal)) {
                    LOG.warn("There is either a category circular reference identified for category id " + childSandBoxVal
                        + " or a product is assigned to both the child category ("
                        + childSandBoxVal + ") and one of its parent categories. Products only need to be assigned to child categories as all parents are added automatically.");
                    continue;
                } else {
                    builtCategories.add(childSandBoxVal);
                }
                
                Set<Long> hierarchy = categoryHierarchy.get(childSandBoxVal);
                if (item.getParent() != null) {
                    //We only want the sandbox parent - if applicable
                    //Long sandBoxVal = sandBoxHelper.getCombinedSandBoxVersionId(CategoryImpl.class, item.getParent());
                    Long sandBoxVal = sandBoxHelper.getSandBoxVersionId(CategoryImpl.class, item.getParent());
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
                    //Long sandBoxVal = sandBoxHelper.getCombinedSandBoxVersionId(CategoryImpl.class, item.getDefaultParent());
                    Long sandBoxVal = sandBoxHelper.getSandBoxVersionId(CategoryImpl.class, item.getDefaultParent());
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
            readFullCategoryHierarchy(nextLevel, builtCategories);
            // Remove duplicates to prevent from overriding entries that have been built out.
            nextLevel.keySet().removeAll(categoryHierarchy.keySet());
        }
        categoryHierarchy.putAll(nextLevel);
    }

}
