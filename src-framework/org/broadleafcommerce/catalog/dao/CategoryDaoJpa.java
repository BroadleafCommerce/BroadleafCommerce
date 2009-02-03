package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Category;
import org.springframework.stereotype.Repository;

@Repository("categoryDao")
public class CategoryDaoJpa implements CategoryDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Category maintainCategory(Category category) {
        if (category.getId() == null) {
            em.persist(category);
        } else {
        	category = em.merge(category);
        }
        return category;
    }

    @Override
    public Category readCategoryById(Long categoryId) {
        return em.find(Category.class, categoryId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Category> readAllCategories() {
        Query query = em.createNamedQuery("READ_ALL_CATEGORIES");
        query.setHint("org.hibernate.cacheable", true);
        return (List<Category>) query.getResultList();
    }

	@Override
	@SuppressWarnings("unchecked")
	public List<Category> readAllSubCategories(Category category) {
        Query query = em.createNamedQuery("READ_ALL_SUBCATEGORIES");
        query.setParameter("parentCategory", category);
        return (List<Category>) query.getResultList();
	}

}
