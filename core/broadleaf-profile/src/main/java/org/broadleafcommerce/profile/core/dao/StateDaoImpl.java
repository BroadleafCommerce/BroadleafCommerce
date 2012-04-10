/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.core.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.State;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

@Repository("blStateDao")
public class StateDaoImpl implements StateDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    /**
	 * @deprecated Use {@link #findStatesByAbbreviation(String)} instead
	 */
    public State findStateByAbbreviation(String abbreviation) {
    	List<State> states = findStatesByAbbreviation(abbreviation);
    	if (states == null || states.size() == 0) {
    		return null;
    	} else {
    		return states.get(0);
    	}
    }
    
    public State findStateById(Long id) {
    	return (State) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.core.domain.State"), id);
    }
    
    @SuppressWarnings("unchecked")
    public List<State> findStatesByAbbreviation(String abbreviation) {
    	Query query = em.createNamedQuery("BC_FIND_STATES_BY_ABBREVIATION");
    	query.setParameter("abbreviation", abbreviation);
    	query.setHint(QueryHints.HINT_CACHEABLE, true);
    	return query.getResultList();
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

    public State create() {
        return (State) entityConfiguration.createEntityInstance(State.class.getName());
    }
    
    public State save(State state) {
    	return em.merge(state);
    }
}
