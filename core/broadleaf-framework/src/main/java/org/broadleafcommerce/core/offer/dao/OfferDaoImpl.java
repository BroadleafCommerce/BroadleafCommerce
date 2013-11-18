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
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

        // Automatically Added or (Automatically Added is null and deliveryType is Automatic)
        criteria.add(Restrictions.or(Restrictions.eq("automaticallyAdded", true),
                Restrictions.and(Restrictions.isNull("automaticallyAdded"),
                        Restrictions.eq("deliveryType", "AUTOMATIC"))));

        criteria.setCacheable(true);

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
