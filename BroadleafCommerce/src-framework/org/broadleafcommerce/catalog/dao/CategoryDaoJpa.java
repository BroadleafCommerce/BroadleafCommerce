package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

public class CategoryDaoJpa implements CategoryDao {

    private JpaTemplate jpaTemplate;

    private EntityConfiguration entityConfiguration;

    public Category maintainCategory(final Category category) {
        return (Category) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Category retCategory = category;
                if (category.getId() == null) {
                    em.persist(retCategory);
                } else {
                    retCategory = em.merge(retCategory);
                }
                return retCategory;
            }
        });
    }

    @Override
    public Category readCategoryById(final Long categoryId) {
        return (Category) this.jpaTemplate.execute(new JpaCallback() {
            @SuppressWarnings("unchecked")
            public Object doInJpa(EntityManager em) throws PersistenceException {
                return em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.catalog.domain.Category"), categoryId);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Category> readAllCategories() {
        return (List<Category>) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_ALL_CATEGORIES");
                query.setHint("org.hibernate.cacheable", true);
                return query.getResultList();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Category> readAllSubCategories(final Category category) {
        return (List<Category>) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_ALL_SUBCATEGORIES");
                query.setParameter("parentCategory", category);
                return query.getResultList();
            }
        });
    }

    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.jpaTemplate = new JpaTemplate(emf);
    }

    public void setEntityConfiguration(EntityConfiguration entityConfiguration) {
        this.entityConfiguration = entityConfiguration;
    }
}
