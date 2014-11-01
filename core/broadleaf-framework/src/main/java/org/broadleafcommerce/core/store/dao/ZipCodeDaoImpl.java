/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.store.dao;

import org.broadleafcommerce.core.store.domain.ZipCode;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blZipCodeDao")
public class ZipCodeDaoImpl implements ZipCodeDao {

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public ZipCode findZipCodeByZipCode(Integer zipCode) {
        Query query = em.createNamedQuery("BC_FIND_ZIP_CODE_BY_ZIP_CODE");
        query.setHint("org.hibernate.cacheable", true);
        query.setParameter("zipCode", zipCode);
        List<ZipCode> result = query.getResultList();
        return (result.size() > 0) ? result.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public ZipCode findBestZipCode(String pCity, String pCounty, String pState, Integer pZipCode, Long pZipGeo) {
        // If we have a zip geo, use it
        if ( pZipGeo != null ) {
            Query query = em.createNamedQuery("FIND_ZIP_WITH_GEO");
            query.setHint("org.hibernate.cacheable", true);
            query.setParameter("geo", pZipGeo);
            query.setParameter("city", pCity);
            query.setParameter("zipCode", pZipCode);
            query.setParameter("state", pState);
            List<ZipCode> result = query.getResultList();
            if (result.size() > 0) {
                return result.get(0);
            }
        }

        // If we have a county, try and find a match
        if ( pCounty != null && !"".equals(pCounty.trim()) ) {
            Query query = em.createNamedQuery("FIND_ZIP_WITH_COUNTY");
            query.setHint("org.hibernate.cacheable", true);
            query.setParameter("county", pCounty);
            query.setParameter("city", pCity);
            query.setParameter("zipCode", pZipCode);
            query.setParameter("state", pState);
            List<ZipCode> result = query.getResultList();
            if (result.size() > 0) {
                return result.get(0);
            }
        }

        {
            // first try for exact match with city, state, zip
            Query query = em.createNamedQuery("FIND_ZIP_WITH_CITY_STATE_ZIP");
            query.setHint("org.hibernate.cacheable", true);
            query.setParameter("city", pCity);
            query.setParameter("zipCode", pZipCode);
            query.setParameter("state", pState);
            List<ZipCode> result = query.getResultList();
            if (result.size() > 0) {
                return result.get(0);
            }
        }

        {
            // now try soundex match with soundex(city),state,zip
            Query query = em.createNamedQuery("FIND_ZIP_WITH_SOUNDEX");
            query.setHint("org.hibernate.cacheable", true);
            query.setParameter("city", pCity);
            query.setParameter("zipCode", pZipCode);
            query.setParameter("state", pState);
            List<ZipCode> result = query.getResultList();
            if (result.size() > 0) {
                return result.get(0);
            }
        }

        {
            // now try state and zip
            Query query = em.createNamedQuery("FIND_ZIP_WITH_STATE_ZIP");
            query.setHint("org.hibernate.cacheable", true);
            query.setParameter("zipCode", pZipCode);
            query.setParameter("state", pState);
            List<ZipCode> result = query.getResultList();
            if (result.size() > 0) {
                return result.get(0);
            }
        }

        {
            // finally just try state
            Query query = em.createNamedQuery("FIND_ZIP_WITH_STATE");
            query.setHint("org.hibernate.cacheable", true);
            query.setParameter("state", pState);
            List<ZipCode> result = query.getResultList();
            if (result.size() > 0) {
                return result.get(0);
            }
        }

        return null;
    }
}
