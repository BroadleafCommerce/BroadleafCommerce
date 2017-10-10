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
package org.broadleafcommerce.common.i18n.dao;

import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Repository("blISODao")
public class ISODaoImpl implements ISODao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public ISOCountry findISOCountryByAlpha2Code(String alpha2) {
        return (ISOCountry) em.find(ISOCountryImpl.class, alpha2);
    }

    @SuppressWarnings("unchecked")
    public List<ISOCountry> findISOCountries() {
        Query query = em.createNamedQuery("BC_FIND_ISO_COUNTRIES");
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    public ISOCountry save(ISOCountry isoCountry) {
        return em.merge(isoCountry);
    }
}
