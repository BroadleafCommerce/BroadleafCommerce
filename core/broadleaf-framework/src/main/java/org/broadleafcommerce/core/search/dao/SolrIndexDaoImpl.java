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

import java.util.List;

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
    public List<Long> readProductIdsByCategory(Long categoryId) {
        TypedQuery<Long> query = em.createNamedQuery("BC_READ_PRODUCT_IDS_BY_CATEGORY", Long.class);
        query.setParameter("categoryId", categoryId);
        //don't cache query results - it's a waste of ehcache since we're caching this in another way at the caller

        return query.getResultList();
    }

}
