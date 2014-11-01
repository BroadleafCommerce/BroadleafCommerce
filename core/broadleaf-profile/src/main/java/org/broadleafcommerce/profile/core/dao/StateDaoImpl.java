/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blStateDao")
public class StateDaoImpl implements StateDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public State findStateByAbbreviation(String abbreviation) {
        return (State) em.find(StateImpl.class, abbreviation);
    }

    @SuppressWarnings("unchecked")
    public List<State> findStates() {
        Query query = em.createNamedQuery("BC_FIND_STATES");
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<State> findStates(String countryAbbreviation) {
        Query query = em.createNamedQuery("BC_FIND_STATES_BY_COUNTRY_ABBREVIATION");
        query.setParameter("countryAbbreviation", countryAbbreviation);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    public Country findCountryByShortName(String shortName) {
        return (Country) em.find(CountryImpl.class, shortName);
    }

    @SuppressWarnings("unchecked")
    public List<Country> findCountries() {
        Query query = em.createNamedQuery("BC_FIND_COUNTRIES");
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    public State create() {
        return (State) entityConfiguration.createEntityInstance(State.class.getName());
    }
    
    public State save(State state) {
        return em.merge(state);
    }
}
