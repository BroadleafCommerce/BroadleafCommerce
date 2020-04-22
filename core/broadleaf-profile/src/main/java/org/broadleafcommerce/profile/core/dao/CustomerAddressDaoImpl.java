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

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerAddressImpl;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository("blCustomerAddressDao")
public class CustomerAddressDaoImpl implements CustomerAddressDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    @SuppressWarnings("unchecked")
    public List<CustomerAddress> readActiveCustomerAddressesByCustomerId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ACTIVE_CUSTOMER_ADDRESSES_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        query.setParameter("archived", 'N');
        return query.getResultList();
    }

    @Override
    public CustomerAddress save(CustomerAddress customerAddress) {
            return em.merge(customerAddress);
    }

    @Override
    public CustomerAddress create() {
        return (CustomerAddress) entityConfiguration.createEntityInstance(CustomerAddress.class.getName());
    }

    @Override
    public List<CustomerAddress> readBatchCustomerAddresses(int start, int pageSize) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<CustomerAddress> criteria = builder.createQuery(CustomerAddress.class);
        Root<CustomerAddressImpl> customer = criteria.from(CustomerAddressImpl.class);
        criteria.select(customer);

        TypedQuery<CustomerAddress> query = em.createQuery(criteria);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.CustomerAddress");

        return query.getResultList();
    }


    @Override
    public Long readNumberOfAddresses() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        criteria.select(builder.count(criteria.from(CustomerAddressImpl.class)));
        TypedQuery<Long> query = em.createQuery(criteria);
        return query.getSingleResult();
    }

    @Override
    public CustomerAddress readCustomerAddressById(Long customerAddressId) {
        return em.find(CustomerAddressImpl.class, customerAddressId);
    }

    @Override
    public void makeCustomerAddressDefault(Long customerAddressId, Long customerId) {
        // Throws a RuntimeException if the CustomerAddress is not found
        CustomerAddress customerAddress = readCustomerAddressByIdAndCustomerId(customerAddressId, customerId);

        if (customerAddress != null) {
            clearDefaultAddressForCustomer(customerId);

            em.refresh(customerAddress);
            customerAddress.getAddress().setDefault(true);

            save(customerAddress);
        }
    }

    @Override
    public CustomerAddress readCustomerAddressByIdAndCustomerId(Long customerAddressId, Long customerId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<CustomerAddress> criteria = builder.createQuery(CustomerAddress.class);
        Root<CustomerAddressImpl> customerAddress = criteria.from(CustomerAddressImpl.class);
        criteria.select(customerAddress);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(customerAddress.get("id"), customerAddressId));
        predicates.add(builder.equal(customerAddress.get("customer").get("id"), customerId));

        criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        TypedQuery<CustomerAddress> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.CustomerAddress");

        return query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    protected void clearDefaultAddressForCustomer(Long customerId) {
        // This has to be done in two queries because MySQL doesn't support updates on a table with a subquery on the same table
        Query query = em.createNamedQuery("BC_READ_DEFAULT_ADDRESS_IDS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        List<Long> addressIds = query.getResultList();
        Query update = em.createNamedQuery("BC_CLEAR_DEFAULT_ADDRESS_BY_IDS");
        update.setParameter("addressIds", addressIds);
        update.executeUpdate();
    }

    @Override
    public void deleteCustomerAddressById(Long customerAddressId) {
        CustomerAddress customerAddress = readCustomerAddressById(customerAddressId);
        if (customerAddress != null) {
            em.remove(customerAddress);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public CustomerAddress findDefaultCustomerAddress(Long customerId) {
        Query query = em.createNamedQuery("BC_FIND_DEFAULT_ADDRESS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        List<CustomerAddress> customerAddresses = query.getResultList();
        return customerAddresses.isEmpty() ? null : customerAddresses.get(0);
    }
}
