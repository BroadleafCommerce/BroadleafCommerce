package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

@Repository("productDao")
public class ProductDaoJpa implements ProductDao {

    private JpaTemplate jpaTemplate;

    private EntityConfiguration entityConfiguration;

    @Override
    public Product maintainProduct(final Product product) {
        return (Product) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Product retProduct = product;
                if (retProduct.getId() == null) {
                    em.persist(retProduct);
                } else {
                    retProduct = em.merge(retProduct);
                }
                return retProduct;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Product readProductById(final Long productId) {
        return (Product) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                return em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.catalog.domain.Product"), productId);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Product> readProductsByName(final String searchName) {
        return (List<Product>) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_PRODUCTS_BY_NAME");
                query.setParameter("name", searchName + "%");
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
