/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.config.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.cache.AbstractCacheMissAware;
import org.broadleafcommerce.common.cache.PersistentRetrieval;
import org.broadleafcommerce.common.config.domain.MessageSource;
import org.broadleafcommerce.common.config.domain.MessageSourceImpl;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminator;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * This DAO enables access to manage Message Sources that can be stored in the database.
 * <p/>
 * @author Elbert Bautista (elbertbautista)
 */
@Repository("blMessageSourceDao")
public class MessageSourceDaoImpl extends AbstractCacheMissAware implements MessageSourceDao {

    protected static final Log LOG = LogFactory.getLog(SystemPropertiesDaoImpl.class);

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public MessageSource readById(Long id) {
        return em.find(MessageSourceImpl.class, id);
    }

    @Override
    public MessageSource saveMessageSource(MessageSource messageSource) {
        return em.merge(messageSource);
    }

    @Override
    public void deleteMessageSource(MessageSource messageSource) {
        em.remove(messageSource);
    }

    @Override
    public List<MessageSource> readAllMessageSources() {
        TypedQuery<MessageSource> query = em.createNamedQuery("BC_READ_ALL_MESSAGE_SOURCES", MessageSource.class);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public MessageSource readMessageSourceByNameAndLocale(final String name, @Nonnull final Locale locale) {
        return getCachedObject(MessageSource.class, "blMessageSourceNullCheckCache", "MESSAGE_SOURCE_MISSING_CACHE_HIT_RATE", new PersistentRetrieval<MessageSource>() {
            @Override
            public MessageSource retrievePersistentObject() {
                CriteriaBuilder builder = em.getCriteriaBuilder();
                CriteriaQuery<MessageSource> criteria = builder.createQuery(MessageSource.class);

                Root<MessageSourceImpl> root = criteria.from(MessageSourceImpl.class);

                criteria.select(root);
                criteria.where(builder.and(
                        builder.equal(root.get("name").as(String.class), name),
                        builder.equal(root.get("locale").as(String.class), locale.getLocaleCode()))
                );

                TypedQuery<MessageSource> query = em.createQuery(criteria);
                query.setHint(QueryHints.HINT_CACHEABLE, true);

                try {
                    return query.getSingleResult();
                } catch (javax.persistence.NoResultException e) {
                    //no message source for name and locale found
                    return null;
                }

            }
        }, name, getSite());
    }

    @Override
    public void removeFromCache(MessageSource messageSource) {
        String site = "";
        if (messageSource instanceof SiteDiscriminator && ((SiteDiscriminator) messageSource).getSiteDiscriminator() != null) {
            site = String.valueOf(((SiteDiscriminator) messageSource).getSiteDiscriminator());
        }
        super.removeItemFromCache("blMessageSourceNullCheckCache", messageSource.getName(), site);
    }

    @Override
    public MessageSource createNewMessageSource() {
        return (MessageSource)entityConfiguration.createEntityInstance(MessageSource.class.getName());
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
