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
package org.broadleafcommerce.core.search.redirect.dao;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.search.redirect.domain.SearchRedirect;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by ppatel.
 */
@Repository("blSearchRedirectDao")
public class SearchRedirectDaoImpl implements SearchRedirectDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    protected Long currentDateResolution = 10000L;
    protected Date cachedDate = SystemTime.asDate();

    protected Date getCurrentDateAfterFactoringInDateResolution() {
        Date returnDate = SystemTime.getCurrentDateWithinTimeResolution(cachedDate, currentDateResolution);
        if (returnDate != cachedDate) {
            if (SystemTime.shouldCacheDate()) {
                cachedDate = returnDate;
            }
        }
        return returnDate;
    }

    @Override
    public SearchRedirect findSearchRedirectBySearchTerm(String searchTerm) {
        Query query;
        query = em.createNamedQuery("BC_READ_SEARCH_URL");
        query.setParameter("searchTerm", searchTerm);
        query.setParameter("now", getCurrentDateAfterFactoringInDateResolution());
        query.setMaxResults(1);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        List<SearchRedirect> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public Long getCurrentDateResolution() {
        return currentDateResolution;
    }

    public void setCurrentDateResolution(Long currentDateResolution) {
        this.currentDateResolution = currentDateResolution;
    }

}
