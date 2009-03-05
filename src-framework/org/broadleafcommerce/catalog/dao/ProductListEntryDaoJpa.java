package org.broadleafcommerce.catalog.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.catalog.domain.ProductListEntry;
import org.springframework.stereotype.Repository;

@Repository("productListEntryDao")
public class ProductListEntryDaoJpa implements ProductListEntryDao {

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Override
    public ProductListEntry maintainProductListAssociation(ProductListEntry productListEntry) {
        if (productListEntry.getId() == null) {
            em.persist(productListEntry);
        } else {
            productListEntry = em.merge(productListEntry);
        }
        return productListEntry;
    }

    @Override
    public ProductListEntry readProductListAssociationById(Long productListAssociationId) {
        return em.find(ProductListEntry.class, productListAssociationId);
    }
}
