/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.redirect.dao;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.search.redirect.domain.SearchRedirect;
import org.broadleafcommerce.core.search.redirect.domain.SearchRedirectImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by ppatel.
 */
@Repository("blSearchRedirectDao")
public class SearchRedirectDaoImpl implements SearchRedirectDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Value("${searchRedirect.is.null.activeStartDate.active:false}")
    protected boolean isNullActiveStartDateActive;

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

    private CriteriaQuery<SearchRedirect> buildFindSearchRedirectBySearchTermCriteria(String searchTerm) {
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

    public Long getCurrentDateResolution() {
        return currentDateResolution;
    }

    public void setCurrentDateResolution(Long currentDateResolution) {
        this.currentDateResolution = currentDateResolution;
    }

}
