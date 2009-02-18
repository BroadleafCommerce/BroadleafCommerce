package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

@Repository("skuDao")
public class SkuDaoJpa implements SkuDao {

    private JpaTemplate jpaTemplate;

    private EntityConfiguration entityConfiguration;

    @Override
    public Sku maintainSku(final Sku sku) {
        return (Sku) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Sku retSku = sku;
                if (retSku.getId() == null) {
                    em.persist(retSku);
                } else {
                    retSku = em.merge(retSku);
                }
                return retSku;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Sku readSkuById(final Long skuId) {
        return (Sku) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                return em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.catalog.domain.Sku"), skuId);
            }
        });
    }

    @Override
    public Sku readFirstSku() {
        return (Sku) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_FIRST_SKU");
                return query.getSingleResult();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Sku> readAllSkus() {
        return (List<Sku>) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_ALL_SKUS");
                return query.getResultList();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Sku> readSkusByProductId(final Long productId) {
        return (List<Sku>) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_SKUS_BY_CATEGORY_ID");
                query.setParameter("productId", productId);
                return query.getResultList();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Sku> readSkusById(final List<Long> ids) {
        return (List<Sku>) this.jpaTemplate.execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery("READ_SKUS_BY_ID");
                query.setParameter("skuIds", ids);
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
