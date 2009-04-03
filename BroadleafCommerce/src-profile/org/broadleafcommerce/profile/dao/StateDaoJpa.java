package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Country;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("stateDao")
public class StateDaoJpa implements StateDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    @SuppressWarnings("unchecked")
    public State findStateByAbbreviation(String abbreviation) {
        return (State) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.State"), abbreviation);
    }

    @SuppressWarnings("unchecked")
    public List<State> findStates() {
        Query query = em.createNamedQuery("BC_FIND_STATES");
        query.setHint("org.hibernate.cacheable", true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Country findCountryByShortName(String shortName) {
        return (Country) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Country"), shortName);
    }

    @SuppressWarnings("unchecked")
    public List<Country> findCountries() {
        Query query = em.createNamedQuery("BC_FIND_COUNTRIES");
        query.setHint("org.hibernate.cacheable", true);
        return query.getResultList();
    }
}
