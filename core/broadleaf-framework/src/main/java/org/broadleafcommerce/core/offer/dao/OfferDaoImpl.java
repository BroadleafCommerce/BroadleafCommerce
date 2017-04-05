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
package org.broadleafcommerce.core.offer.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OfferInfo;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.domain.ProratedOrderItemAdjustment;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessageImpl;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

@Repository("blOfferDao")
public class OfferDaoImpl implements OfferDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

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
    public Offer create() {
        return ((Offer) entityConfiguration.createEntityInstance(Offer.class.getName()));
    }

    @Override
    public OfferInfo createOfferInfo() {
        return ((OfferInfo) entityConfiguration.createEntityInstance(OfferInfo.class.getName()));
    }

    @Override
    public CandidateOrderOffer createCandidateOrderOffer() {
        return ((CandidateOrderOffer) entityConfiguration.createEntityInstance(CandidateOrderOffer.class.getName()));
    }

    @Override
    public CandidateItemOffer createCandidateItemOffer() {
        return ((CandidateItemOffer) entityConfiguration.createEntityInstance(CandidateItemOffer.class.getName()));
    }

    @Override
    public CandidateFulfillmentGroupOffer createCandidateFulfillmentGroupOffer() {
        return ((CandidateFulfillmentGroupOffer) entityConfiguration.createEntityInstance(CandidateFulfillmentGroupOffer.class.getName()));
    }

    @Override
    public OrderItemAdjustment createOrderItemAdjustment() {
        return ((OrderItemAdjustment) entityConfiguration.createEntityInstance(OrderItemAdjustment.class.getName()));
    }

    @Override
    public OrderItemPriceDetailAdjustment createOrderItemPriceDetailAdjustment() {
        return ((OrderItemPriceDetailAdjustment) entityConfiguration.createEntityInstance(OrderItemPriceDetailAdjustment.class.getName()));
    }

    @Override
    public OrderAdjustment createOrderAdjustment() {
        return ((OrderAdjustment) entityConfiguration.createEntityInstance(OrderAdjustment.class.getName()));
    }

    @Override
    public FulfillmentGroupAdjustment createFulfillmentGroupAdjustment() {
        return ((FulfillmentGroupAdjustment) entityConfiguration.createEntityInstance(FulfillmentGroupAdjustment.class.getName()));
    }

    @Override
    public void delete(Offer offer) {
        ((Status) offer).setArchived('Y');
        em.merge(offer);
    }

    @Override
    public void delete(OfferInfo offerInfo) {
        if (!em.contains(offerInfo)) {
            offerInfo = (OfferInfo) em.find(entityConfiguration.lookupEntityClass(OfferInfo.class.getName()), offerInfo.getId());
        }
        em.remove(offerInfo);
    }

    @Override
    public Offer save(Offer offer) {
        return em.merge(offer);
    }

    @Override
    public OfferInfo save(OfferInfo offerInfo) {
        return em.merge(offerInfo);
    }

    @Override
    public ProratedOrderItemAdjustment save(ProratedOrderItemAdjustment adjustment) {
        return em.merge(adjustment);
    }

    @Override
    public List<Offer> readAllOffers() {
        Query query = em.createNamedQuery("BC_READ_ALL_OFFERS");
        return query.getResultList();
    }

    @Override
    public Offer readOfferById(Long offerId) {
        return em.find(OfferImpl.class, offerId);
    }

    @Override
    public List<Offer> readOffersByAutomaticDeliveryType() {
        //TODO change this to a JPA criteria
        Criteria criteria = ((HibernateEntityManager) em).getSession().createCriteria(OfferImpl.class);

        Date myDate = getCurrentDateAfterFactoringInDateResolution();

        Calendar c = Calendar.getInstance();
        c.setTime(myDate);
        c.add(Calendar.DATE, +1);
        criteria.add(Restrictions.lt("startDate", c.getTime()));
        c = Calendar.getInstance();
        c.setTime(myDate);
        c.add(Calendar.DATE, -1);
        criteria.add(Restrictions.or(Restrictions.isNull("endDate"), Restrictions.gt("endDate", c.getTime())));
        criteria.add(Restrictions.or(Restrictions.eq("archiveStatus.archived", 'N'),
                Restrictions.isNull("archiveStatus.archived")));
        
        criteria.add(Restrictions.eq("automaticallyAdded", true));

        criteria.setCacheable(true);
        criteria.setCacheRegion("query.Offer");

        return criteria.list();
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
