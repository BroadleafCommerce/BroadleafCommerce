/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.file.dao;

import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.domain.StaticAssetImpl;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Created by bpolster.
 */
@Repository("blStaticAssetDao")
public class StaticAssetDaoImpl implements StaticAssetDao {

    private static SandBox DUMMY_SANDBOX = new SandBoxImpl();
    {
        DUMMY_SANDBOX.setId(-1l);
    }

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public StaticAsset readStaticAssetById(Long id) {
        return em.find(StaticAssetImpl.class, id);
    }
    
    public List<StaticAsset> readAllStaticAssets() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<StaticAsset> criteria = builder.createQuery(StaticAsset.class);
        Root<StaticAssetImpl> handler = criteria.from(StaticAssetImpl.class);
        criteria.select(handler);
        try {
            return em.createQuery(criteria).getResultList();
        } catch (NoResultException e) {
            return new ArrayList<StaticAsset>();
        }
    }

    @Override
    public StaticAsset readStaticAssetByFullUrl(String fullUrl, SandBox targetSandBox) {
        TypedQuery<StaticAsset> query;
        if (targetSandBox == null) {
            query = em.createNamedQuery("BC_READ_STATIC_ASSET_BY_FULL_URL_AND_TARGET_SANDBOX_NULL", StaticAsset.class);
            query.setParameter("fullUrl", fullUrl);
        } else {
            query = em.createNamedQuery("BC_READ_STATIC_ASSET_BY_FULL_URL", StaticAsset.class);
            query.setParameter("targetSandbox", targetSandBox);
            query.setParameter("fullUrl", fullUrl);
        }
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        List<StaticAsset> results = query.getResultList();
        if (CollectionUtils.isEmpty(results)) {
            return null;
        } else {
            return results.iterator().next();
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
