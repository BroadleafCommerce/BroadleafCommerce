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
package org.broadleafcommerce.core.offer.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.module.BroadleafModuleRegistration;
import org.broadleafcommerce.common.module.ModulePresentUtil;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.offer.domain.OfferAuditImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository("blOfferAuditDao")
public class OfferAuditDaoImpl implements OfferAuditDao {

    protected static final Log LOG = LogFactory.getLog(OfferAuditDaoImpl.class);

    private static final Long NULL_ACCOUNT_ID = null;
    private static final Long NULL_CUSTOMER_ID = null;

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected Long currentDateResolution = 3600000L;
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
    public Long countUsesByCustomer(Order order, Long customerId, Long offerId) {
        return countUsesByCustomer(order, customerId, offerId, null);
    }

    @Override
    public Long countUsesByAccount(Order order, Long accountId, Long offerId) {
        return countUsesByAccount(order, accountId, offerId, null);
    }

    @Override
    public Long countUsesByAccount(Order order, Long accountId, Long offerId, Long minimumDaysPerUsage) {
        return countUsesByAccountOrCustomer(order, NULL_CUSTOMER_ID, accountId, offerId, minimumDaysPerUsage);
    }

    @Override
    public Long countUsesByCustomer(Order order, Long customerId, Long offerId, Long minimumDaysPerUsage) {
        return countUsesByAccountOrCustomer(order, customerId, NULL_ACCOUNT_ID, offerId, minimumDaysPerUsage);
    }

    protected Long countUsesByAccountOrCustomer(
            Order order,
            Long customerId,
            Long accountId,
            Long offerId,
            Long minimumDaysPerUsage
    ) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<OfferAuditImpl> root = criteria.from(OfferAuditImpl.class);
        Root<OrderImpl> orderRoot = criteria.from(OrderImpl.class);
        Join<Object, Object> parentOrder = null;
        if (ModulePresentUtil.isPresent(BroadleafModuleRegistration.BroadleafModuleEnum.OMS)) {
            parentOrder = orderRoot.join("embeddedOmsOrder", JoinType.LEFT).join("parentOrder", JoinType.LEFT);
        }
        criteria.select(builder.count(root));

        Predicate customerOrAccountPredicate = null;

        if (customerId != null) {
            customerOrAccountPredicate = builder.equal(root.get("customerId"), customerId);
        } else if (accountId != null) {
            customerOrAccountPredicate = builder.equal(root.get("accountId"), accountId);
        } else {
            LOG.debug("Count uses by account or customer called without an account or a customer.");
            return 0L;
        }

        List<Predicate> restrictions = new ArrayList<>();
        restrictions.add(
                builder.and(
                        customerOrAccountPredicate,
                        builder.equal(root.get("offerId"), offerId),
                        builder.or(
                                builder.isNull(root.get("orderId")),
                                builder.and(
                                        builder.notEqual(root.get("orderId"), getOrderId(order)),
                                        builder.notEqual(orderRoot.get("status"), OrderStatus.CANCELLED.getType()),
                                        builder.equal(orderRoot.get("id"), root.get("orderId"))
                                )
                        ),
                        getOmsOrderPredicate(builder, orderRoot, parentOrder)
                )
        );

        if (minimumDaysPerUsage != null && minimumDaysPerUsage != 0L) {
            Date currentDate = getCurrentDateAfterFactoringInDateResolution();

            Calendar previousCalendar = new GregorianCalendar();

            previousCalendar.setTime(currentDate);
            previousCalendar.add(Calendar.DAY_OF_YEAR, -minimumDaysPerUsage.intValue());

            restrictions.add(builder.between(root.<Date>get("redeemedDate"), previousCalendar.getTime(), currentDate));
        }

        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        try {
            return em.createQuery(criteria).getSingleResult();
        } catch (Exception e) {
            LOG.error("Error counting offer uses by customer.", e);
            return null;
        }
    }

    protected Predicate getOmsOrderPredicate(
            CriteriaBuilder builder,
            Root<OrderImpl> orderRoot,
            Join<Object, Object> parentOrder
    ) {
        if (ModulePresentUtil.isPresent(BroadleafModuleRegistration.BroadleafModuleEnum.OMS)) {
            return builder.or(
                    builder.isNull(orderRoot.get("embeddedOmsOrder").get("parentOrder")),
                    builder.notEqual(parentOrder.get("status"), OrderStatus.CANCELLED.getType())
            );
        } else {
            return builder.isTrue(builder.literal(Boolean.TRUE));
        }
    }

    protected Long getOrderId(Order order) {
        return order.getId();
    }

    @Deprecated
    @Override
    public Long countUsesByCustomer(Long customerId, Long offerId) {
        TypedQuery<Long> query = new TypedQueryBuilder<>(OfferAuditImpl.class, "offerAudit", OfferAudit.class)
                .addRestriction("offerAudit.customerId", "=", customerId)
                .addRestriction("offerAudit.offerId", "=", offerId)
                .toCountQuery(em);

        return query.getSingleResult();
    }

    @Override
    public Long countOfferCodeUses(Order order, Long offerCodeId) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT count(oa.id) AS countOfferCodeUses ")
                .append("FROM OrderImpl o ");
        if (ModulePresentUtil.isPresent(BroadleafModuleRegistration.BroadleafModuleEnum.OMS)) {
            sqlBuilder.append("LEFT JOIN OrderImpl o2 ON o.embeddedOmsOrder.parentOrder.id = o2.id ");
        }
        sqlBuilder.append("LEFT JOIN OfferAuditImpl oa ON oa.orderId = o.id ")
                .append("WHERE (oa.orderId IS NULL OR oa.orderId <> :orderId ) ")
                .append("AND oa.offerCodeId = :offerCodeId ")
                .append("AND (oa.orderId IS NULL OR o.status <> :orderStatus) ");
        if (ModulePresentUtil.isPresent(BroadleafModuleRegistration.BroadleafModuleEnum.OMS)) {
            sqlBuilder.append("AND (o.embeddedOmsOrder.parentOrder.id IS NULL OR o2.status <> :orderStatus) ");
        }
        try {
            return (Long) em.createQuery(sqlBuilder.toString())
                    .setParameter("orderId", order.getId())
                    .setParameter("offerCodeId", offerCodeId)
                    .setParameter("orderStatus", OrderStatus.CANCELLED.getType())
                    .getSingleResult();
        } catch (Exception e) {
            LOG.error("Error counting offer code uses.", e);
            return null;
        }
    }

    @Deprecated
    @Override
    public Long countOfferCodeUses(Long offerCodeId) {
        TypedQuery<Long> query = new TypedQueryBuilder<>(OfferAuditImpl.class, "offerAudit", OfferAudit.class)
                .addRestriction("offerAudit.offerCodeId", "=", offerCodeId)
                .toCountQuery(em);

        return query.getSingleResult();
    }

    @Override
    public List<OfferAudit> readOfferAuditsByOrderId(Long orderId) {
        TypedQuery<OfferAudit> query = new TypedQueryBuilder<>(OfferAuditImpl.class, "offerAudit", OfferAudit.class)
                .addRestriction("offerAudit.orderId", "=", orderId)
                .toQuery(em);

        return query.getResultList();
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
