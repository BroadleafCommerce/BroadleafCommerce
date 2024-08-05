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
package org.broadleafcommerce.core.rating.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.BatchRetrieveDao;
import org.broadleafcommerce.core.rating.domain.RatingDetail;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.RatingSummaryImpl;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository("blRatingSummaryDao")
public class RatingSummaryDaoImpl extends BatchRetrieveDao implements RatingSummaryDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public RatingSummary createSummary() {
        return entityConfiguration.createEntityInstance(RatingSummary.class.getName(), RatingSummary.class);
    }
    
    @Override
    public RatingSummary createSummary(String itemId, RatingType type) {
        RatingSummary summary = createSummary();
        summary.setItemId(itemId);
        summary.setRatingType(type);
        return summary;
    }
    
    @Override
    public RatingDetail createDetail() {
        return entityConfiguration.createEntityInstance(RatingDetail.class.getName(), RatingDetail.class);
    }
    
    @Override
    public RatingDetail createDetail(RatingSummary ratingSummary, Double rating, Date submittedDate, Customer customer) {
        RatingDetail detail = createDetail();
        detail.setRatingSummary(ratingSummary);
        detail.setRating(rating);
        detail.setRatingSubmittedDate(submittedDate);
        detail.setCustomer(customer);
        return detail;
    }
    
    @Override
    public void deleteRatingSummary(final RatingSummary summary) {
        RatingSummary lSummary = summary;
        if (!em.contains(lSummary)) {
            lSummary = em.find(RatingSummaryImpl.class, lSummary.getId());
        }
        em.remove(lSummary);
    }

    @Override
    public RatingSummary saveRatingSummary(final RatingSummary summary) {
        summary.resetAverageRating();
        return em.merge(summary);
    }

    @Override
    public List<RatingSummary> readRatingSummaries(final List<String> itemIds, final RatingType type) {
        final Query query = em.createNamedQuery("BC_READ_RATING_SUMMARIES_BY_ITEM_ID_AND_TYPE");
        query.setParameter("ratingType", type.getType());
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");
        List<RatingSummary> ratings = batchExecuteReadQuery(query, itemIds, "itemIds");

        return ratings;
    }

    @Override
    public RatingSummary readRatingSummary(final String itemId, final RatingType type) {
        final Query query = em.createNamedQuery("BC_READ_RATING_SUMMARY_BY_ITEM_ID_AND_TYPE");
        query.setParameter("itemId", itemId);
        query.setParameter("ratingType", type.getType());
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");
        RatingSummary ratingSummary = null;

        try {
            ratingSummary = (RatingSummary) query.getSingleResult();
        } catch (NoResultException e) {
            // ignore
        }

        return ratingSummary;
    }

    @Override
    public RatingDetail readRating(final Long customerId, final Long ratingSummaryId) {
        final Query query = em.createNamedQuery("BC_READ_RATING_DETAIL_BY_CUSTOMER_ID_AND_RATING_SUMMARY_ID");
        query.setParameter("customerId", customerId);
        query.setParameter("ratingSummaryId", ratingSummaryId);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");
        RatingDetail ratingDetail = null;

        try {
            ratingDetail = (RatingDetail) query.getSingleResult();
        } catch (NoResultException e) {
            // ignore
        }

        return ratingDetail;
    }

    @Override
    public ReviewDetail readReview(final Long customerId, final Long ratingSummaryId) {
        final Query query = em.createNamedQuery("BC_READ_REVIEW_DETAIL_BY_CUSTOMER_ID_AND_RATING_SUMMARY_ID");
        query.setParameter("customerId", customerId);
        query.setParameter("ratingSummaryId", ratingSummaryId);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");
        ReviewDetail reviewDetail = null;

        try {
            reviewDetail = (ReviewDetail) query.getSingleResult();
        } catch (NoResultException e) {
            // ignore
        }

        return reviewDetail;
    }

}
