/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.core.search.domain.SearchIntercept;
import org.broadleafcommerce.core.search.redirect.dao.SearchRedirectDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @deprecated Replaced in functionality by {@link SearchRedirectDaoImpl}
 */
@Repository("blSearchInterceptDao")
@Deprecated
public class SearchInterceptDaoImpl implements SearchInterceptDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Override
    public SearchIntercept findInterceptByTerm(String term) {
        Query query = em.createNamedQuery("BC_READ_SEARCH_INTERCEPT_BY_TERM");
        query.setParameter("searchTerm", term);
        SearchIntercept result;
        try {
            result = (SearchIntercept) query.getSingleResult();
        } catch (NoResultException e) {
            result = null;
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SearchIntercept> findAllIntercepts() {
        Query query = em.createNamedQuery("BC_READ_ALL_SEARCH_INTERCEPTS");
        List<SearchIntercept> result;
        try {
            result = query.getResultList();
        } catch (NoResultException e) {
            result = null;
        }

        return result;
        
    }

    @Override
    public void createIntercept(SearchIntercept intercept) {
        em.persist(intercept);
    }

    @Override
    public void deleteIntercept(SearchIntercept intercept) {
        em.remove(intercept);
    }

    @Override
    public void updateIntercept(SearchIntercept intercept) {
        em.merge(intercept);
    }

}
