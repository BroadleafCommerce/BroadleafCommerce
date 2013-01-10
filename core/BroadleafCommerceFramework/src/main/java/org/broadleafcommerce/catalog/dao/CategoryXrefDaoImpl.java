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
package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.CategoryXref;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blCategoryXrefDao")
public class CategoryXrefDaoImpl implements CategoryXrefDao {
    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected String queryCacheableKey = "org.hibernate.cacheable";

    @SuppressWarnings("unchecked")
    public List<CategoryXref> readXrefsByCategoryId(Long categoryId){
        Query query = em.createNamedQuery("BC_READ_CATEGORY_XREF_BY_CATEGORYID");
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
        
    }
    
    @SuppressWarnings("unchecked")
    public List<CategoryXref> readXrefsBySubCategoryId(Long subCategoryId){
        Query query = em.createNamedQuery("BC_READ_CATEGORY_XREF_BY_SUBCATEGORYID");
        query.setParameter("subCategoryId", subCategoryId);
        return query.getResultList();
    }
    
    public CategoryXref readXrefByIds(Long categoryId, Long subCategoryId){
        Query query = em.createNamedQuery("BC_READ_CATEGORY_XREF_BY_IDS");
        query.setParameter("categoryId", categoryId);
        query.setParameter("subCategoryId", subCategoryId);
        return (CategoryXref)query.getSingleResult();
    }
    
    public CategoryXref save(CategoryXref categoryXref){
        return em.merge(categoryXref);
    }
    
    public void delete(CategoryXref categoryXref){
        if (!em.contains(categoryXref)) {
            categoryXref = readXrefByIds(categoryXref.getCategoryXrefPK().getCategory().getId(),
                                         categoryXref.getCategoryXrefPK().getSubCategory().getId());
        }
        em.remove(categoryXref);        
    }
    
    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
    
    
}
