/*-
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.file.dao;

import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetImpl;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Created by bpolster.
 */
@Repository("blStaticAssetDao")
public class StaticAssetDaoImpl implements StaticAssetDao {

    private static final SandBox DUMMY_SANDBOX = new SandBoxImpl();
    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;
    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    @Resource(name = "blStaticAssetDaoQueryExtensionManager")
    protected StaticAssetDaoQueryExtensionManager queryExtensionManager;

    {
        DUMMY_SANDBOX.setId(-1l);
    }

    @Override
    public StaticAsset readStaticAssetById(Long id) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<StaticAsset> criteria = builder.createQuery(StaticAsset.class);
        Root<StaticAssetImpl> handler = criteria.from(StaticAssetImpl.class);
        criteria.select(handler);
        List<Predicate> restrictions = new ArrayList<>();
        restrictions.add(builder.equal(handler.get("id"), id));
        try {
            if (queryExtensionManager != null) {
                queryExtensionManager.getProxy().setup(StaticAssetImpl.class, null);
                queryExtensionManager.getProxy().refineRetrieve(
                        StaticAssetImpl.class, null, builder, criteria, handler, restrictions
                );
            }
            criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

            TypedQuery<StaticAsset> query = em.createQuery(criteria);
            query.setHint(QueryHints.HINT_CACHEABLE, true);
            List<StaticAsset> response = query.getResultList();
            if (response.size() > 0) {
                return response.get(0);
            }
            return null;
        } finally {
            if (queryExtensionManager != null) {
                queryExtensionManager.getProxy().breakdown(StaticAssetImpl.class, null);
            }
        }
    }

    public List<StaticAsset> readAllStaticAssets() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<StaticAsset> criteria = builder.createQuery(StaticAsset.class);
        Root<StaticAssetImpl> handler = criteria.from(StaticAssetImpl.class);
        criteria.select(handler);
        List<Predicate> restrictions = new ArrayList<>();
        List<Order> sorts = new ArrayList<Order>();
        try {
            if (queryExtensionManager != null) {
                queryExtensionManager.getProxy().setup(StaticAssetImpl.class, null);
                queryExtensionManager.getProxy().refineRetrieve(
                        StaticAssetImpl.class, null, builder, criteria, handler, restrictions
                );
                queryExtensionManager.getProxy().refineOrder(
                        StaticAssetImpl.class, null, builder, criteria, handler, sorts
                );
            }
            criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
            return em.createQuery(criteria).getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {
            if (queryExtensionManager != null) {
                queryExtensionManager.getProxy().breakdown(StaticAssetImpl.class, null);
            }
        }
    }

    @Override
    public Long readTotalStaticAssetCount() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        criteria.select(builder.count(criteria.from(StaticAssetImpl.class)));

        TypedQuery<Long> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getSingleResult();
    }

    @Override
    public StaticAsset readStaticAssetByFullUrl(String fullUrl) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<StaticAsset> criteria = builder.createQuery(StaticAsset.class);
        Root<StaticAssetImpl> handler = criteria.from(StaticAssetImpl.class);
        criteria.select(handler);

        List<Predicate> restrictions = new ArrayList<>();
        List<Order> sorts = new ArrayList<Order>();
        restrictions.add(builder.equal(handler.get("fullUrl"), fullUrl));
        try {
            if (queryExtensionManager != null) {
                queryExtensionManager.getProxy().setup(StaticAssetImpl.class, null);
                queryExtensionManager.getProxy().refineRetrieve(
                        StaticAssetImpl.class, null, builder, criteria, handler, restrictions
                );
                queryExtensionManager.getProxy().refineOrder(
                        StaticAssetImpl.class, null, builder, criteria, handler, sorts
                );
            }
            criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
            if (!org.apache.commons.collections.CollectionUtils.isEmpty(sorts)) {
                criteria.orderBy(sorts);
            }

            TypedQuery<StaticAsset> query = em.createQuery(criteria);
            query.setHint(QueryHints.HINT_CACHEABLE, true);
            List<StaticAsset> response = query.getResultList();
            if (response.size() > 0) {
                return response.get(0);
            }
            return null;
        } finally {
            if (queryExtensionManager != null) {
                queryExtensionManager.getProxy().breakdown(StaticAssetImpl.class, null);
            }
        }
    }

    @Override
    public StaticAsset addOrUpdateStaticAsset(StaticAsset asset, boolean clearLevel1Cache) {
        if (clearLevel1Cache) {
            em.detach(asset);
        }
        return em.merge(asset);
    }

    @Override
    public void delete(StaticAsset asset) {
        if (!em.contains(asset)) {
            asset = readStaticAssetById(asset.getId());
        }
        em.remove(asset);
    }

}
