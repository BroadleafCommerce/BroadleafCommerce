package org.broadleafcommerce.catalog.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.catalog.domain.ProductList;
import org.springframework.stereotype.Repository;

@Repository("productListDao")
public class ProductListDaoJpa implements ProductListDao {

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Override
    public ProductList maintainProductList(ProductList productList) {
        if (productList.getId() == null) {
            em.persist(productList);
        } else {
        	productList = em.merge(productList);
        }
        return productList;
    }

    @Override
    public ProductList readProductListById(Long productListId) {
        return em.find(ProductList.class, productListId);
    }
}
