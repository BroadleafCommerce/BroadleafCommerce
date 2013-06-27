/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.locale.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by bpolster.
 */
@Repository("blLocaleDao")
public class LocaleDaoImpl implements LocaleDao {
    private static final Log LOG = LogFactory.getLog(LocaleDaoImpl.class);

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    /**
     * @return The locale for the passed in code
     */
    @Override
    public Locale findLocaleByCode(String localeCode) {
        Query query = em.createNamedQuery("BC_READ_LOCALE_BY_CODE");
        query.setParameter("localeCode", localeCode);
        query.setHint(org.hibernate.ejb.QueryHints.HINT_CACHEABLE, true);
        List<Locale> localeList = (List<Locale>) query.getResultList();
        if (localeList.size() >= 1) {
            if (localeList.size() > 1) {
                LOG.warn("Locale code " + localeCode + " exists for more than one locale");
            }
            return localeList.get(0);
        }
        return null;
    }

    /**
     * Returns the page template with the passed in id.
     *
     * @return The default locale
     */
    @Override
    public Locale findDefaultLocale() {
        Query query = em.createNamedQuery("BC_READ_DEFAULT_LOCALE");
        query.setHint(org.hibernate.ejb.QueryHints.HINT_CACHEABLE, true);
        List<Locale> localeList = (List<Locale>) query.getResultList();
        if (localeList.size() >= 1) {
            if (localeList.size() > 1) {
                LOG.warn("There is more than one default locale configured");
            }
            return localeList.get(0);
        }
        return null;
    }

    /**
     * Returns all supported BLC locales.
     * @return
     */
    public List<Locale> findAllLocales() {
        Query query = em.createNamedQuery("BC_READ_ALL_LOCALES");
        query.setHint(org.hibernate.ejb.QueryHints.HINT_CACHEABLE, true);
        return (List<Locale>) query.getResultList();
    }
    
    @Override
    public Locale save(Locale locale){
        return em.merge(locale);
    }
    
}
