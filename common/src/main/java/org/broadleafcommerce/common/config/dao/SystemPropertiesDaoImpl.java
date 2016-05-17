/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.config.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.cache.AbstractCacheMissAware;
import org.broadleafcommerce.common.cache.PersistentRetrieval;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.config.domain.SystemPropertyImpl;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminator;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * This DAO enables access to manage system properties that can be stored in the database.
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/20/12
 */
@Repository("blSystemPropertiesDao")
public class SystemPropertiesDaoImpl extends AbstractCacheMissAware implements SystemPropertiesDao {

    protected static final Log LOG = LogFactory.getLog(SystemPropertiesDaoImpl.class);

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blSystemPropertyDaoQueryExtensionManager")
    protected SystemPropertyDaoQueryExtensionManager queryExtensionManager;

    @Override
    public SystemProperty readById(Long id) {
        return em.find(SystemPropertyImpl.class, id);
    }

    @Override
    public SystemProperty saveSystemProperty(SystemProperty systemProperty) {
        return em.merge(systemProperty);
    }

    @Override
    public void deleteSystemProperty(SystemProperty systemProperty) {
        em.remove(systemProperty);
    }

    @Override
    public List<SystemProperty> readAllSystemProperties() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SystemProperty> criteria = builder.createQuery(SystemProperty.class);
        Root<SystemPropertyImpl> handler = criteria.from(SystemPropertyImpl.class);
        criteria.select(handler);
        List<Predicate> restrictions = new ArrayList<Predicate>();
        List<Order> sorts = new ArrayList<Order>();
        try {
            if (queryExtensionManager != null) {
                queryExtensionManager.getProxy().setup(SystemPropertyImpl.class, null);
                queryExtensionManager.getProxy().refineRetrieve(SystemPropertyImpl.class, null, builder, criteria, handler, restrictions);
                queryExtensionManager.getProxy().refineOrder(SystemPropertyImpl.class, null, builder, criteria, handler, sorts);
            }
            criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
            return em.createQuery(criteria).getResultList();
        } catch (NoResultException e) {
            LOG.error(e);
            return new ArrayList<SystemProperty>();
        } finally {
            if (queryExtensionManager != null) {
                queryExtensionManager.getProxy().breakdown(SystemPropertyImpl.class, null);
            }
        }
    }

    @Override
    public SystemProperty readSystemPropertyByName(final String name) {
        return getCachedObject(SystemProperty.class, "blSystemPropertyNullCheckCache", "SYSTEM_PROPERTY_MISSING_CACHE_HIT_RATE", new PersistentRetrieval<SystemProperty>() {
            @Override
            public SystemProperty retrievePersistentObject() {
                CriteriaBuilder builder = em.getCriteriaBuilder();
                CriteriaQuery<SystemProperty> criteria = builder.createQuery(SystemProperty.class);
                Root<SystemPropertyImpl> handler = criteria.from(SystemPropertyImpl.class);
                criteria.select(handler);

                List<Predicate> restrictions = new ArrayList<Predicate>();
                restrictions.add(builder.equal(handler.get("name"), name));

                try {
                    if (queryExtensionManager != null) {
                        queryExtensionManager.getProxy().setup(SystemPropertyImpl.class, null);
                        queryExtensionManager.getProxy().refineRetrieve(SystemPropertyImpl.class, null, builder, criteria, handler, restrictions);
                    }
                    criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

                    TypedQuery<SystemProperty> query = em.createQuery(criteria);
                    query.setHint(QueryHints.HINT_CACHEABLE, true);
                    List<SystemProperty> response = query.getResultList();
                    if (response.size() > 0) {
                        ExtensionResultHolder<List> resultHolder = new ExtensionResultHolder<>();
                        if (queryExtensionManager != null) {
                            ExtensionResultStatusType resultStatusType = queryExtensionManager.getProxy().refineResults(SystemPropertyImpl.class, null, response, resultHolder);
                            if (!resultStatusType.equals(ExtensionResultStatusType.NOT_HANDLED)) {
                                return (SystemProperty) resultHolder.getResult().get(0);
                            }
                        }
                        return response.get(0);
                    }
                    return null;
                } finally {
                    if (queryExtensionManager != null) {
                        queryExtensionManager.getProxy().breakdown(SystemPropertyImpl.class, null);
                    }
                }
            }
        }, name, getSite());
    }

    @Override
    public void removeFromCache(SystemProperty systemProperty) {
        String site = "";
        if (systemProperty instanceof SiteDiscriminator && ((SiteDiscriminator) systemProperty).getSiteDiscriminator() != null) {
            site = String.valueOf(((SiteDiscriminator) systemProperty).getSiteDiscriminator());
        }
        super.removeItemFromCache("blSystemPropertyNullCheckCache", systemProperty.getName(), site);
    }

    @Override
    public SystemProperty createNewSystemProperty() {
        return (SystemProperty) entityConfiguration.createEntityInstance(SystemProperty.class.getName());
    }

    @Override
    protected Log getLogger() {
        return LOG;
    }

    protected String getSite() {
        String site = "";
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            if (brc.getSite() != null) {
                site = String.valueOf(brc.getSite().getId());
            }
        }
        return site;
    }
}
