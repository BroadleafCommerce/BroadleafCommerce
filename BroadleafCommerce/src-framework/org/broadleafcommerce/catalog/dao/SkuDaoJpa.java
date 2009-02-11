package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Sku;
import org.springframework.stereotype.Repository;

@Repository("skuDao")
public class SkuDaoJpa implements SkuDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Sku maintainSku(Sku sku) {
        if (sku.getId() == null) {
            em.persist(sku);
        } else {
            sku = em.merge(sku);
        }
        return sku;
    }

    @Override
    public Sku readSkuById(Long skuId) {
        return em.find(Sku.class, skuId);
    }

    @Override
    public Sku readFirstSku() {
        Query query = em.createNamedQuery("READ_FIRST_SKU");
        return (Sku) query.getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Sku> readAllSkus() {
        Query query = em.createNamedQuery("READ_ALL_SKUS");
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Sku> readSkusByProductId(Long productId) {
        Query query = em.createNamedQuery("READ_SKUS_BY_CATEGORY_ID");
        query.setParameter("productId", productId);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Sku> readSkusById(List<Long> ids) {
        Query query = em.createNamedQuery("READ_SKUS_BY_ID");
        query.setParameter("skuIds", ids);
        return query.getResultList();
    }
}
