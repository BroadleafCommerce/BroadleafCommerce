package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("categoryDao")
public class CategoryDaoJpa implements CategoryDao {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    public Category maintainCategory(Category category) {
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

    @SuppressWarnings("unchecked")
    public List<Category> readAllCategories() {
        Query query = em.createNamedQuery("READ_ALL_CATEGORIES");
        query.setHint("org.hibernate.cacheable", true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Category> readAllSubCategories(final Category category) {
        Query query = em.createNamedQuery("READ_ALL_SUBCATEGORIES");
        query.setParameter("parentCategory", category);
        return query.getResultList();
    }
}
