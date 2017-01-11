/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.util.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

/**
 * @author Jeff Fischer
 */
@Repository("blResourcePurgeDao")
public class ResourcePurgeDaoImpl implements ResourcePurgeDao {

    public static final int RESTRICT_IN_CLAUSE_MAX_SIZE = 800;
    
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Override
    public List<Order> findCarts(String[] names, OrderStatus[] statuses, Date dateCreatedMinThreshold, Boolean isPreview,
            List<Long> excludedIds) {
        TypedQuery<Order> query = buildCartQuery(names, statuses, dateCreatedMinThreshold, isPreview, Order.class, excludedIds);
        return query.getResultList();
    }

    @Override
    public List<Order> findCarts(String[] names, OrderStatus[] statuses, Date dateCreatedMinThreshold, Boolean isPreview, int startPos, int length,
            List<Long> excludedIds) {
        TypedQuery<Order> query = buildCartQuery(names, statuses, dateCreatedMinThreshold, isPreview, Order.class, excludedIds);
        query.setFirstResult(startPos);
        query.setMaxResults(length);
        return query.getResultList();
    }

    @Override
    public Long findCartsCount(String[] names, OrderStatus[] statuses, Date dateCreatedMinThreshold, Boolean isPreview,
            List<Long> excludedIds) {
        TypedQuery<Long> query = buildCartQuery(names, statuses, dateCreatedMinThreshold, isPreview, Long.class, excludedIds);
        return query.getSingleResult();
    }

    @Override
    public List<Customer> findCustomers(Date dateCreatedMinThreshold, Boolean registered, Boolean deactivated, Boolean isPreview, List<Long> excludedIds) {
        TypedQuery<Customer> query = buildCustomerQuery(dateCreatedMinThreshold, registered, deactivated, isPreview, Customer.class, excludedIds);
        return query.getResultList();
    }

    @Override
    public List<Customer> findCustomers(Date dateCreatedMinThreshold, Boolean registered, Boolean deactivated, Boolean isPreview, int startPos, int length, List<Long> excludedIds) {
        TypedQuery<Customer> query = buildCustomerQuery(dateCreatedMinThreshold, registered, deactivated, isPreview,
                Customer.class, excludedIds);
        query.setFirstResult(startPos);
        query.setMaxResults(length);
        return query.getResultList();
    }

    @Override
    public Long findCustomersCount(Date dateCreatedMinThreshold, Boolean registered, Boolean deactivated, Boolean isPreview, List<Long> excludedIds) {
        TypedQuery<Long> query = buildCustomerQuery(dateCreatedMinThreshold, registered, deactivated, isPreview, Long.class, excludedIds);
        return query.getSingleResult();
    }

    protected <T> TypedQuery<T> buildCustomerQuery(Date dateCreatedMinThreshold, Boolean registered, Boolean deactivated, Boolean isPreview, Class<T> returnType,
            List<Long> excludedIds) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(returnType);
        Root<CustomerImpl> root = criteria.from(CustomerImpl.class);
        if (Long.class.equals(returnType)) {
            criteria.select((Selection<? extends T>) builder.count(root));
        } else {
            criteria.select((Selection<? extends T>) root);
        }

        //find only customers that do not have any orders, otherwise a purge would fail because of referential integrity
        Subquery<Long> subquery = criteria.subquery(Long.class);
        Root orderRoot = subquery.from(OrderImpl.class);
        subquery.select(builder.count(orderRoot));
        subquery.where(builder.equal(orderRoot.get("customer"),root));

        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(subquery, 0L));
        if (registered != null) {
            if (registered) {
                restrictions.add(builder.isTrue(root.get("registered").as(Boolean.class)));
            } else {
                restrictions.add(builder.or(builder.isNull(root.get("registered")),
                        builder.isFalse(root.get("registered").as(Boolean.class))));
            }
        }
        if (deactivated != null) {
            if (deactivated) {
                restrictions.add(builder.isTrue(root.get("deactivated").as(Boolean.class)));
            } else {
                restrictions.add(builder.or(builder.isNull(root.get("deactivated")),
                        builder.isFalse(root.get("deactivated").as(Boolean.class))));
            }
        }
        if (dateCreatedMinThreshold != null) {
            restrictions.add(builder.lessThan(root.get("auditable").get("dateCreated").as(Date.class), dateCreatedMinThreshold));
        }
        if (isPreview != null) {
            if (isPreview) {
                restrictions.add(builder.isTrue(root.get("previewable").get("isPreview").as(Boolean.class)));
            } else {
                restrictions.add(builder.or(builder.isNull(root.get("previewable").get("isPreview")),
                        builder.isFalse(root.get("previewable").get("isPreview").as(Boolean.class))));
            }
        }
        if (excludedIds != null && excludedIds.size() > 0) {
                applyLimitedInClause(excludedIds, builder, root, restrictions);
        }
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return em.createQuery(criteria);
    }

    protected <T> void applyLimitedInClause(List<Long> ids, CriteriaBuilder builder, Root<T> root, List<Predicate> restrictions) {
        List<List<Long>> listsOfExcludeIds = Lists.partition(ids, RESTRICT_IN_CLAUSE_MAX_SIZE);
        List<Predicate> inRestrictions = new ArrayList<Predicate>();
        for (List<Long> idSetToExclude : listsOfExcludeIds) {
            inRestrictions.add(builder.not(root.get("id").in(idSetToExclude)));
        }
        restrictions.add(builder.or(inRestrictions.toArray(new Predicate[inRestrictions.size()])));
    }
    
    protected <T> TypedQuery<T> buildCartQuery(String[] names, OrderStatus[] statuses, Date dateCreatedMinThreshold, Boolean isPreview, Class<T> returnType,
            List<Long> excludedIds) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(returnType);
        Root<OrderImpl> root = criteria.from(OrderImpl.class);
        if (Long.class.equals(returnType)) {
            criteria.select((Selection<? extends T>) builder.count(root));
        } else {
            criteria.select((Selection<? extends T>) root);
        }
        List<Predicate> restrictions = new ArrayList<Predicate>();
        List<String> statusList = new ArrayList<String>();
        if (statuses != null) {
            for (OrderStatus status : statuses) {
                statusList.add(status.getType());
            }
        } else {
            statusList.add("IN_PROCESS");
        }
        restrictions.add(root.get("status").in(statusList));
        if (names != null) {
            restrictions.add(root.get("name").in(Arrays.asList(names)));
        }
        if (dateCreatedMinThreshold != null) {
            restrictions.add(builder.lessThan(root.get("auditable").get("dateCreated").as(Date.class), dateCreatedMinThreshold));
        }
        if (isPreview != null) {
            if (isPreview) {
                restrictions.add(builder.isTrue(root.get("previewable").get("isPreview").as(Boolean.class)));
            } else {
                restrictions.add(builder.or(builder.isNull(root.get("previewable").get("isPreview")),
                        builder.isFalse(root.get("previewable").get("isPreview").as(Boolean.class))));
            }
        }
        if (excludedIds != null && excludedIds.size() > 0) {
            applyLimitedInClause(excludedIds, builder, root, restrictions);
        }
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return em.createQuery(criteria);
    }
}
