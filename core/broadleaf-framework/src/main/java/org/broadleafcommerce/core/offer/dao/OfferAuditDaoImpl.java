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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.TQRestriction;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.offer.domain.OfferAuditImpl;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository("blOfferAuditDao")
public class OfferAuditDaoImpl implements OfferAuditDao {
    
    protected static final Log LOG = LogFactory.getLog(OfferAuditDaoImpl.class);

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public OfferAudit create() {
        return ((OfferAudit) entityConfiguration.createEntityInstance(OfferAudit.class.getName()));
    }

    @Override
    public void delete(final OfferAudit offerAudit) {
        OfferAudit loa = offerAudit;
        
        if (!em.contains(loa)) {
            loa = readAuditById(offerAudit.getId());
        }
        
        em.remove(loa);
    }

    @Override
    public OfferAudit save(final OfferAudit offerAudit) {
        return em.merge(offerAudit);
    }

    @Override
    public OfferAudit readAuditById(final Long offerAuditId) {
        return em.find(OfferAuditImpl.class, offerAuditId);
    }

    @Override
    public Long countUsesByCustomer(Long orderId, Long customerId, Long offerId) {
        TQRestriction isNotAlreadyAppliedToOrder = new TQRestriction("offerAudit.orderId", "<>", orderId);
        TQRestriction orderIdIsNull = new TQRestriction("offerAudit.orderId", "is", null);
        TQRestriction isNotAlreadyAppliedToOrderOrOrderIdIsNull = new TQRestriction(TQRestriction.Mode.OR)
                .addChildRestriction(isNotAlreadyAppliedToOrder)
                .addChildRestriction(orderIdIsNull);
        TypedQuery<Long> query = new TypedQueryBuilder<>(OfferAudit.class, "offerAudit")
                .addRestriction("offerAudit.customerId", "=", customerId)
                .addRestriction("offerAudit.offerId", "=", offerId)
                .addRestriction(isNotAlreadyAppliedToOrderOrOrderIdIsNull)
                .toCountQuery(em);

        return query.getSingleResult();
    }
    
    @Deprecated
    @Override
    public Long countUsesByCustomer(Long customerId, Long offerId) {
        TypedQuery<Long> query = new TypedQueryBuilder<>(OfferAudit.class, "offerAudit")
                .addRestriction("offerAudit.customerId", "=", customerId)
                .addRestriction("offerAudit.offerId", "=", offerId)
                .toCountQuery(em);

        return query.getSingleResult();
    }

    @Override
    public Long countOfferCodeUses(Long orderId, Long offerCodeId) {
        TQRestriction isNotAlreadyAppliedToOrder = new TQRestriction("offerAudit.orderId", "<>", orderId);
        TQRestriction orderIdIsNull = new TQRestriction("offerAudit.orderId", "is", null);
        TQRestriction isNotAlreadyAppliedToOrderOrOrderIdIsNull = new TQRestriction(TQRestriction.Mode.OR)
                .addChildRestriction(isNotAlreadyAppliedToOrder)
                .addChildRestriction(orderIdIsNull);
        TypedQuery<Long> query = new TypedQueryBuilder<>(OfferAudit.class, "offerAudit")
                .addRestriction("offerAudit.offerCodeId", "=", offerCodeId)
                .addRestriction(isNotAlreadyAppliedToOrderOrOrderIdIsNull)
                .toCountQuery(em);

        return query.getSingleResult();
    }
    
    @Deprecated
    @Override
    public Long countOfferCodeUses(Long offerCodeId) {
        TypedQuery<Long> query = new TypedQueryBuilder<>(OfferAudit.class, "offerAudit")
                .addRestriction("offerAudit.offerCodeId", "=", offerCodeId)
                .toCountQuery(em);

        return query.getSingleResult();
    }

    @Override
    public List<OfferAudit> readOfferAuditsByOrderId(Long orderId) {
        TypedQuery<OfferAudit> query = new TypedQueryBuilder<>(OfferAudit.class, "offerAudit")
                .addRestriction("offerAudit.orderId", "=", orderId)
                .toQuery(em);
        
        return query.getResultList();
    }

}
