package org.broadleafcommerce.store.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.store.domain.Store;
import org.springframework.stereotype.Repository;

@Repository("blStoreDao")
public class StoreDaoImpl implements StoreDao {

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public Store readStoreByStoreCode(final String storeCode) {
        Query query = em.createNamedQuery("FIND_STORE_BY_STORE_CODE");
        query.setParameter("abbreviation", storeCode.toUpperCase());
        //TODO use the property injection for "org.hibernate.cacheable" like the other daos
        query.setHint("org.hibernate.cacheable", true);
        List result = query.getResultList();
        return (result.size() > 0) ? (Store) result.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public List<Store> readAllStores() {
        Query query = em.createNamedQuery("BC_FIND_ALL_STORES");
        query.setHint("org.hibernate.cacheable", true);
        List results = query.getResultList();
        return results;
    }

}