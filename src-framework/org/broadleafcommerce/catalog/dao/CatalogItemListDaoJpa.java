package org.broadleafcommerce.catalog.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.catalog.domain.CatalogItemList;
import org.springframework.stereotype.Repository;

@Repository("catalogItemListDao")
public class CatalogItemListDaoJpa implements CatalogItemListDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public CatalogItemList maintainCatalogItemList(CatalogItemList catalogItemList) {
        if (catalogItemList.getId() == null) {
            em.persist(catalogItemList);
        } else {
        	catalogItemList = em.merge(catalogItemList);
        }
        return catalogItemList;
    }

    @Override
    public CatalogItemList readCatalogItemListById(Long catalogItemListId) {
        return em.find(CatalogItemList.class, catalogItemListId);
    }
}
