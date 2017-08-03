/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.url.dao;

import org.broadleafcommerce.cms.url.domain.URLHandler;
import org.broadleafcommerce.cms.url.domain.URLHandlerImpl;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ppatel.
 */
@Repository("blURLHandlerDao")
public class URlHandlerDaoImpl implements URLHandlerDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public URLHandler findURLHandlerByURI(String uri) {
        TypedQuery<URLHandler> query = em.createNamedQuery("BC_READ_BY_INCOMING_URL", URLHandler.class);
        query.setParameter("incomingURL", uri);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        List<URLHandler> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            return results.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<URLHandler> findAllRegexURLHandlers() {
        TypedQuery<URLHandler> query = em.createNamedQuery("BC_READ_ALL_REGEX_HANDLERS", URLHandler.class);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public List<URLHandler> findURLHandlersByDestination(String uri) {
        TypedQuery<URLHandler> query = em.createNamedQuery("BC_READ_BY_DESTINATION_URL", URLHandler.class);
        query.setParameter("newURL", uri);
        return query.getResultList();
    }

    @Override
    public URLHandler findURLHandlerById(Long id) {
        return em.find(URLHandlerImpl.class, id);
    }

    @Override
    public List<URLHandler> findAllURLHandlers() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<URLHandler> criteria = builder.createQuery(URLHandler.class);
        Root<URLHandlerImpl> handler = criteria.from(URLHandlerImpl.class);
        criteria.select(handler);
        TypedQuery<URLHandler> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<URLHandler>();
        }
    }

    public URLHandler saveURLHandler(URLHandler handler) {
        return em.merge(handler);
    }

    @Override
    public void savePartialURLHandler(URLHandler handler) {
        Query query = em.createNamedQuery("BC_PARTIAL_UPDATE_BY_ID");
        query.setParameter("id", handler.getId())
                .setParameter("incomingURL", handler.getIncomingURL())
                .setParameter("newURL", handler.getNewURL());
        query.executeUpdate();
    }

    @Override
    public void deleteURLHandler(URLHandler urlHandler) {
        Query query = em.createNamedQuery("BC_DELETE_BY_ID");
        query.setParameter("id", urlHandler.getId());
        query.executeUpdate();
    }

}
