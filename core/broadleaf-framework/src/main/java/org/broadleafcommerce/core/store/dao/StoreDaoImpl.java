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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.core.store.domain.Store;
import org.broadleafcommerce.core.store.domain.StoreImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

@Repository("blStoreDao")
public class StoreDaoImpl implements StoreDao {

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    public Store readStoreById(Long id) {
        return em.find(StoreImpl.class, id);
    }

    @SuppressWarnings("unchecked")
    public Store readStoreByStoreName(final String storeName) {
        Query query = em.createNamedQuery("BC_FIND_STORE_BY_STORE_NAME");
        query.setParameter("storeName", storeName);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        List result = query.getResultList();
        return (result.size() > 0) ? (Store) result.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public Store readStoreByStoreCode(final String storeCode) {
        Query query = em.createNamedQuery("BC_FIND_STORE_BY_STORE_NAME");
        query.setParameter("storeName", storeCode.toUpperCase());
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        List result = query.getResultList();
        return (result.size() > 0) ? (Store) result.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public List<Store> readAllStores() {
        Query query = em.createNamedQuery("BC_FIND_ALL_STORES");
        query.setParameter("archived", 'N');
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Store> readAllStoresByState(final String state) {
        Query query = em.createNamedQuery("BC_FIND_ALL_STORES_BY_STATE");
        query.setParameter("state", state);
        query.setParameter("archived", 'N');
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public Store save(Store store) {
        return em.merge(store);
    }
}
