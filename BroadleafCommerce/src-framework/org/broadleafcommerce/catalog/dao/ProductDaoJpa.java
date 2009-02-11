package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Product;
import org.springframework.stereotype.Repository;

@Repository("productDao")
public class ProductDaoJpa implements ProductDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Product maintainProduct(Product product) {
        if (product.getId() == null) {
            em.persist(product);
        } else {
            product = em.merge(product);
        }
        return product;
    }

    @Override
    public Product readProductById(Long productId) {
        return em.find(Product.class, productId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Product> readProductsByName(String searchName) {
        Query query = em.createNamedQuery("READ_PRODUCTS_BY_NAME");
        query.setParameter("name", searchName + "%");
        return query.getResultList();
    }
}
