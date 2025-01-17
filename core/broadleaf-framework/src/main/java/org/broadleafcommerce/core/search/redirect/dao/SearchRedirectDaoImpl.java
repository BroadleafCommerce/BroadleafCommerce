/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.redirect.dao;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.search.redirect.domain.SearchRedirect;
import org.broadleafcommerce.core.search.redirect.domain.SearchRedirectImpl;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Created by ppatel.
 */
@Repository("blSearchRedirectDao")
public class SearchRedirectDaoImpl implements SearchRedirectDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Value("${searchRedirect.is.null.activeStartDate.active:false}")
    protected boolean isNullActiveStartDateActive;

    @Value("${query.dateResolution.searchredirect:10000}")
    protected Long currentDateResolution;

    protected Date cachedDate = SystemTime.asDate();

    protected Date getCurrentDateAfterFactoringInDateResolution() {
        Date returnDate = SystemTime.getCurrentDateWithinTimeResolution(cachedDate, getCurrentDateResolution());
        if (returnDate != cachedDate) {
            if (SystemTime.shouldCacheDate()) {
                cachedDate = returnDate;
            }
        }
        return returnDate;
    }

    @Override
    public SearchRedirect findSearchRedirectBySearchTerm(String searchTerm) {
        Query query = em.createQuery(buildFindSearchRedirectBySearchTermCriteria(searchTerm));
        query.setMaxResults(1);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        List<SearchRedirect> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            return results.get(0);
        } else {
            return null;
        }
    }

    protected CriteriaQuery<SearchRedirect> buildFindSearchRedirectBySearchTermCriteria(String searchTerm) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SearchRedirect> criteria = builder.createQuery(SearchRedirect.class);
        Root<SearchRedirectImpl> redirect = criteria.from(SearchRedirectImpl.class);

        List<Predicate> restrictions = new ArrayList<>();
        restrictions.add(builder.equal(builder.upper(redirect.<String>get("searchTerm")), searchTerm.toUpperCase()));

        // Add the active start/end date restrictions
        Date currentDate = getCurrentDateAfterFactoringInDateResolution();
        if (isNullActiveStartDateActive) {
            restrictions.add(builder.or(builder.isNull(redirect.get("activeStartDate")),
                    builder.lessThanOrEqualTo(redirect.get("activeStartDate").as(Date.class), currentDate)));
        } else {
            restrictions.add(builder.and(builder.isNotNull(redirect.get("activeStartDate")),
                    builder.lessThanOrEqualTo(redirect.get("activeStartDate").as(Date.class), currentDate)));
        }
        restrictions.add(builder.or(builder.isNull(redirect.get("activeEndDate")),
                builder.greaterThan(redirect.get("activeEndDate").as(Date.class), currentDate)));

        // Add the restrictions to the criteria query
        criteria.select(redirect);
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return criteria.orderBy(builder.asc(redirect.get("searchPriority")));
    }

    @Override
    public Long getCurrentDateResolution() {
        return currentDateResolution;
    }

    @Override
    public void setCurrentDateResolution(Long currentDateResolution) {
        this.currentDateResolution = currentDateResolution;
    }

}
