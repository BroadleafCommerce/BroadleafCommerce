package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.CatalogItem;
import org.springframework.stereotype.Repository;

@Repository("catalogItemDao")
public class CatalogItemDaoJpa implements CatalogItemDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public CatalogItem maintainCatalogItem(CatalogItem catalogItem) {
        if (catalogItem.getId() == null) {
            em.persist(catalogItem);
        } else {
            catalogItem = em.merge(catalogItem);
        }
        return catalogItem;
    }

    @Override
    public CatalogItem readCatalogItemById(Long catalogItemId) {
        return em.find(CatalogItem.class, catalogItemId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CatalogItem> readCatalogItemsByName(String searchName) {
        Query query = em.createNamedQuery("READ_CATALOG_ITEMS_BY_NAME");
        query.setParameter("name", searchName + "%");
        return query.getResultList();
    }
}
