package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("categoryDao")
public class CategoryDaoJpa implements CategoryDao {

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    private String queryCacheableKey = "org.hibernate.cacheable";

    public Category save(Category category) {
        if (category.getId() == null) {
            em.persist(category);
        } else {
            category = em.merge(category);
        }
        return category;
    }

    @SuppressWarnings("unchecked")
    public Category readCategoryById(Long categoryId) {
        return (Category) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.catalog.domain.Category"), categoryId);
    }

    public Category readCategoryByName(String categoryName) {
        Query query = em.createNamedQuery("BC_READ_CATEGORY_BY_NAME");
        query.setParameter("categoryName", categoryName);
        query.setHint(getQueryCacheableKey(), true);
        return (Category)query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Category> readAllCategories() {
        Query query = em.createNamedQuery("BC_READ_ALL_CATEGORIES");
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> readAllProducts() {
        Query query = em.createNamedQuery("BC_READ_ALL_PRODUCTS");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Category> readAllSubCategories(final Category category) {
        Query query = em.createNamedQuery("BC_READ_ALL_SUBCATEGORIES");
        query.setParameter("defaultParentCategory", category);
        return query.getResultList();
    }

    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
}
