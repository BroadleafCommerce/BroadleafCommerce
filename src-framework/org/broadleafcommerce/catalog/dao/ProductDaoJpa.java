package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("productDao")
public class ProductDaoJpa implements ProductDao {

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    public Product maintainProduct(Product product) {
        if (product.getId() == null) {
            em.persist(product);
        } else {
            product = em.merge(product);
        }
        return product;
    }

    @SuppressWarnings("unchecked")
    public Product readProductById(Long productId) {
        return (Product) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.catalog.domain.Product"), productId);
    }

    @SuppressWarnings("unchecked")
    public List<Product> readProductsByName(String searchName) {
        Query query = em.createNamedQuery("BC_READ_PRODUCTS_BY_NAME");
        query.setParameter("name", searchName + "%");
        //TODO externalize this hibernate cache string
        query.setHint("org.hibernate.cacheable", true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> readActiveProductsByCategory(Long categoryId) {
        Query query = em.createNamedQuery("BC_READ_ACTIVE_PRODUCTS_BY_CATEGORY");
        query.setParameter("categoryId", categoryId);
        //TODO externalize this hibernate cache string
        query.setHint("org.hibernate.cacheable", true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<Product> readProductsBySku(Long skuId) {
        Query query = em.createNamedQuery("BC_READ_PRODUCTS_BY_SKU");
        query.setParameter("skuId", skuId);
        //TODO externalize this hibernate cache string
        query.setHint("org.hibernate.cacheable", true);
        return query.getResultList();
	}
}
