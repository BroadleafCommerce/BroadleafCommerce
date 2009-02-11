package org.broadleafcommerce.catalog.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.catalog.domain.ProductListAssociation;
import org.springframework.stereotype.Repository;

@Repository("productListAssociationDao")
public class ProductListAssociationDaoJpa implements ProductListAssociationDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public ProductListAssociation maintainProductListAssociation(ProductListAssociation productListAssociation) {
        if (productListAssociation.getId() == null) {
            em.persist(productListAssociation);
        } else {
        	productListAssociation = em.merge(productListAssociation);
        }
        return productListAssociation;
    }

    @Override
    public ProductListAssociation readProductListAssociationById(Long productListAssociationId) {
        return em.find(ProductListAssociation.class, productListAssociationId);
    }
}
