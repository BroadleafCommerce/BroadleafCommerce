package org.broadleafcommerce.catalog.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("skuDao")
public class SkuDaoJpa implements SkuDao {

    @PersistenceContext
    private EntityManager em;

    @Resource(name = "entityConfiguration")
    private EntityConfiguration entityConfiguration;

    @Override
    public Sku maintainSku(Sku sku) {
        if (sku.getId() == null) {
            em.persist(sku);
        } else {
            sku = em.merge(sku);
        }
        return sku;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Sku readSkuById(Long skuId) {
        return (Sku) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.catalog.domain.Sku"), skuId);
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
