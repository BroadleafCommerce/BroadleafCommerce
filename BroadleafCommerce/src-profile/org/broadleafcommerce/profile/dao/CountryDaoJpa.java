package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Country;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("countryDao")
public class CountryDaoJpa implements CountryDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    private String queryCacheableKey = "org.hibernate.cacheable";

    @SuppressWarnings("unchecked")
    public Country findCountryByAbbreviation(String abbreviation) {
        return (Country) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Country"), abbreviation);
    }

    @SuppressWarnings("unchecked")
    public List<Country> findCountries() {
        Query query = em.createNamedQuery("BC_FIND_COUNTRIES");
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
}
