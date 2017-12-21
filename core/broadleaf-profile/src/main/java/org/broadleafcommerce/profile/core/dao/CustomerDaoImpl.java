/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.dao;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository("blCustomerDao")
public class CustomerDaoImpl implements CustomerDao {

    private static final Log LOG = LogFactory.getLog(CustomerDaoImpl.class);

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public Customer readCustomerById(Long id) {
        return em.find(CustomerImpl.class, id);
    }

    @Override
    public Customer readCustomerByExternalId(String id) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
        Root<? extends Customer> customer = criteria.from(entityConfiguration.lookupEntityClass(Customer.class.getName(), Customer.class));
        criteria.select(customer);
        criteria.where(builder.equal(customer.get("externalId"), id));

        TypedQuery<Customer> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, false);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Customer");
        List<Customer> resultList = query.getResultList();
        return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
    }

    @Override
    public List<Customer> readCustomersByIds(List<Long> ids){
        if (ids == null || ids.size() == 0) {
            return null;
        }
        if (ids.size() > 100) {
            LOG.warn("Not recommended to use the readCustomersByIds method for long lists of customerIds, since " +
                    "Hibernate is required to transform the distinct results. The list of requested" +
                    "customer ids was (" + ids.size() + ") in length.");
        }
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
        Root<CustomerImpl> customer = criteria.from(CustomerImpl.class);
        criteria.select(customer);

        // We only want results that match the customer IDs
        criteria.where(customer.get("id").as(Long.class).in(ids));

        TypedQuery<Customer> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");

        return query.getResultList();
    }

    @Override
    public Customer readCustomerByUsername(String username) {
        return readCustomerByUsername(username, true);
    }

    @Override
    public Customer readCustomerByUsername(String username, Boolean cacheable) {
        List<Customer> customers = readCustomersByUsername(username, cacheable);
        return CollectionUtils.isEmpty(customers) ? null : customers.get(0);
    }

    @Override
    public List<Customer> readCustomersByUsername(String username) {
        return readCustomersByUsername(username, true);
    }

    @Override
    public List<Customer> readCustomersByUsername(String username, Boolean cacheable) {
        TypedQuery<Customer> query = em.createNamedQuery("BC_READ_CUSTOMER_BY_USER_NAME", Customer.class);
        query.setParameter("username", username);
        query.setHint(QueryHints.HINT_CACHEABLE, cacheable);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");
        return query.getResultList();
    }

    @Override
    public Customer readCustomerByEmail(String emailAddress) {
        List<Customer> customers = readCustomersByEmail(emailAddress);
        return CollectionUtils.isEmpty(customers) ? null : customers.get(0);
    }

    @Override
    public List<Customer> readCustomersByEmail(String emailAddress) {
        TypedQuery<Customer> query = em.createNamedQuery("BC_READ_CUSTOMER_BY_EMAIL", Customer.class);
        query.setParameter("email", emailAddress);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");
        return query.getResultList();
    }

    @Override
    public Customer save(Customer customer) {
        return em.merge(customer);
    }

    @Override
    public Customer create() {
        Customer customer =  (Customer) entityConfiguration.createEntityInstance(Customer.class.getName());
        return customer;
    }

    @Override
    public void delete(Customer customer) {
        if (!em.contains(customer)) {
            customer = readCustomerById(customer.getId());
        }
        em.remove(customer);
    }

    @Override
    public List<Customer> readBatchCustomers(int start, int pageSize) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
        Root<CustomerImpl> customer = criteria.from(CustomerImpl.class);
        criteria.select(customer);

        TypedQuery<Customer> query = em.createQuery(criteria);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Customer");

        return query.getResultList();
    }

    @Override
    public Long readNumberOfCustomers() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        criteria.select(builder.count(criteria.from(CustomerImpl.class)));
        TypedQuery<Long> query = em.createQuery(criteria);

        return query.getSingleResult();
    }
}
