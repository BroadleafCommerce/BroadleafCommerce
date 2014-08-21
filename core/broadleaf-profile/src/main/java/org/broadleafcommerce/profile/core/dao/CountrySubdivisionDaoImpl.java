/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.CountrySubdivision;
import org.broadleafcommerce.profile.core.domain.CountrySubdivisionImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Repository("blCountrySubdivisionDao")
public class CountrySubdivisionDaoImpl implements CountrySubdivisionDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public CountrySubdivision findSubdivisionByAbbreviation(String abbreviation) {
        return (CountrySubdivision) em.find(CountrySubdivisionImpl.class, abbreviation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CountrySubdivision> findSubdivisions() {
        Query query = em.createNamedQuery("BC_FIND_COUNTRY_SUBDIVISIONS");
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CountrySubdivision> findSubdivisions(String countryAbbreviation) {
        Query query = em.createNamedQuery("BC_FIND_SUBDIVISIONS_BY_COUNTRY_ABBREVIATION");
        query.setParameter("countryAbbreviation", countryAbbreviation);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public CountrySubdivision create() {
        return (CountrySubdivision) entityConfiguration.createEntityInstance(CountrySubdivision.class.getName());
    }

    @Override
    public CountrySubdivision save(CountrySubdivision state) {
        return em.merge(state);
    }
}

