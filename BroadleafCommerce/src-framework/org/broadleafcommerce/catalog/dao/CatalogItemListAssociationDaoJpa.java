package org.broadleafcommerce.catalog.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.catalog.domain.CatalogItemListAssociation;
import org.springframework.stereotype.Repository;

@Repository("catalogItemListAssociationDao")
public class CatalogItemListAssociationDaoJpa implements CatalogItemListAssociationDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public CatalogItemListAssociation maintainCatalogItemListAssociation(CatalogItemListAssociation catalogItemListAssociation) {
        if (catalogItemListAssociation.getId() == null) {
            em.persist(catalogItemListAssociation);
        } else {
        	catalogItemListAssociation = em.merge(catalogItemListAssociation);
        }
        return catalogItemListAssociation;
    }

    @Override
    public CatalogItemListAssociation readCatalogItemListAssociationById(Long catalogItemListAssociationId) {
        return em.find(CatalogItemListAssociation.class, catalogItemListAssociationId);
    }
}
