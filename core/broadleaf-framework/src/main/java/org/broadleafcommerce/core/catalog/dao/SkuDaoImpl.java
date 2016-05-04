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

package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.logging.SupportLogManager;
import org.broadleafcommerce.common.logging.SupportLogger;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.common.util.DialectHelper;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuFee;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * {@inheritDoc}
 *
 * @author Jeff Fischer
 */
@Repository("blSkuDao")
public class SkuDaoImpl implements SkuDao {

    private static final SupportLogger logger = SupportLogManager.getLogger("Enterprise", SkuDaoImpl.class);

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Resource(name = "blDialectHelper")
    protected DialectHelper dialectHelper;

    @Resource(name = "blSkuDaoExtensionManager")
    protected SkuDaoExtensionManager extensionManager;

    protected Long currentDateResolution = 10000L;
    protected Date cachedDate = SystemTime.asDate();

    @Override
    public Sku save(Sku sku) {
        return em.merge(sku);
    }

    @Override
    public SkuFee saveSkuFee(SkuFee fee) {
        return em.merge(fee);
    }

    @Override
    public Sku readSkuById(Long skuId) {
        return (Sku) em.find(SkuImpl.class, skuId);
    }

    @Override
    public Sku readSkuByExternalId(String externalId) {
        TypedQuery<Sku> query = new TypedQueryBuilder<Sku>(Sku.class, "sku")
                .addRestriction("sku.externalId", "=", externalId)
                .toQuery(em);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Sku readSkuByUpc(String upc) {
        TypedQuery<Sku> query = new TypedQueryBuilder<Sku>(Sku.class, "sku")
                .addRestriction("sku.upc", "=", upc)
                .toQuery(em);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Sku readFirstSku() {
        TypedQuery<Sku> query = em.createNamedQuery("BC_READ_FIRST_SKU", Sku.class);
        return query.getSingleResult();
    }

    @Override
    public List<Sku> readAllSkus() {
        TypedQuery<Sku> query = em.createNamedQuery("BC_READ_ALL_SKUS", Sku.class);
        return query.getResultList();
    }

    @Override
    public List<Sku> readSkusByIds(List<Long> skuIds) {
        if (skuIds == null || skuIds.size() == 0) {
            return null;
        }
        if (skuIds.size() > 100) {
            logger.warn("Not recommended to use the readSkusByIds method for long lists of skuIds, since " +
                    "Hibernate is required to transform the distinct results. The list of requested" +
                    "sku ids was (" + skuIds.size() + ") in length.");
        }
        // Set up the criteria query that specifies we want to return Products
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Sku> criteria = builder.createQuery(Sku.class);
        Root<SkuImpl> sku = criteria.from(SkuImpl.class);

        criteria.select(sku);

        // We only want results that match the sku IDs
        criteria.where(sku.get("id").as(Long.class).in(
                sandBoxHelper.mergeCloneIds(SkuImpl.class,
                        skuIds.toArray(new Long[skuIds.size()]))));
        if (!dialectHelper.isOracle() && !dialectHelper.isSqlServer()) {
            criteria.distinct(true);
        }

        TypedQuery<Sku> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getResultList();
    }

    @Override
    public void delete(Sku sku) {
        if (!em.contains(sku)) {
            sku = readSkuById(sku.getId());
        }
        em.remove(sku);
    }

    @Override
    public Sku create() {
        return (Sku) entityConfiguration.createEntityInstance(Sku.class.getName());
    }

    @Override
    public Long readCountAllActiveSkus() {
        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution);
        return readCountAllActiveSkusInternal(currentDate);
    }

    protected Long readCountAllActiveSkusInternal(Date currentDate) {
        // Set up the criteria query that specifies we want to return a Long
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

        // The root of our search is sku
        Root<SkuImpl> sku = criteria.from(SkuImpl.class);

        // We want the count of products
        criteria.select(builder.count(sku));

        // Ensure the sku is currently active
        List<Predicate> restrictions = new ArrayList<Predicate>();

        // Add the active start/end date restrictions
        restrictions.add(builder.lessThan(sku.get("activeStartDate").as(Date.class), currentDate));
        restrictions.add(builder.or(
                builder.isNull(sku.get("activeEndDate")),
                builder.greaterThan(sku.get("activeEndDate").as(Date.class), currentDate)));

        // Add the restrictions to the criteria query
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        TypedQuery<Long> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.getSingleResult();
    }

    @Override
    public List<Sku> readAllActiveSkus(int page, int pageSize) {
        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution);
        return readAllActiveSkusInternal(page, pageSize, currentDate);
    }

    protected List<Sku> readAllActiveSkusInternal(int page, int pageSize, Date currentDate) {
        // Set up the criteria query that specifies we want to return Products
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Sku> criteria = builder.createQuery(Sku.class);

        // The root of our search is Product
        Root<SkuImpl> sku = criteria.from(SkuImpl.class);

        // Product objects are what we want back
        criteria.select(sku);

        // Ensure the product is currently active
        List<Predicate> restrictions = new ArrayList<Predicate>();

        // Add the active start/end date restrictions
        restrictions.add(builder.lessThan(sku.get("activeStartDate").as(Date.class), currentDate));
        restrictions.add(builder.or(
                builder.isNull(sku.get("activeEndDate")),
                builder.greaterThan(sku.get("activeEndDate").as(Date.class), currentDate)));

        // Add the restrictions to the criteria query
        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

        int firstResult = page * pageSize;
        TypedQuery<Sku> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        return query.setFirstResult(firstResult).setMaxResults(pageSize).getResultList();
    }

    @Override
    public Long getCurrentDateResolution() {
        return currentDateResolution;
    }

    @Override
    public void setCurrentDateResolution(Long currentDateResolution) {
        this.currentDateResolution = currentDateResolution;
    }

    @Override
    public List<Sku> findSkuByURI(String uri) {
        if (extensionManager != null) {
            ExtensionResultHolder holder = new ExtensionResultHolder();
            ExtensionResultStatusType result = extensionManager.getProxy().findSkuByURI(uri, holder);
            if (ExtensionResultStatusType.HANDLED.equals(result)) {
                return (List<Sku>) holder.getResult();
            }
        }
        String skuUrlKey = uri.substring(uri.lastIndexOf('/'));
        String productUrl = uri.substring(0, uri.lastIndexOf('/'));
        Query query;

        query = em.createNamedQuery("BC_READ_SKU_BY_OUTGOING_URL");
        query.setParameter("url", uri);
        query.setParameter("productUrl", productUrl);
        query.setParameter("skuUrlKey", skuUrlKey);
        query.setParameter("currentDate", DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, currentDateResolution));
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Catalog");

        @SuppressWarnings("unchecked")
        List<Sku> results = query.getResultList();
        return results;
    }

}
