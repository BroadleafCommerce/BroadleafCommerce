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
