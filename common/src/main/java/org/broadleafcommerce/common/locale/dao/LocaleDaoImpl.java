/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.locale.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.StringUtil;
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
                LOG.warn("Locale code " + StringUtil.sanitize(localeCode) + " exists for more than one locale");
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
