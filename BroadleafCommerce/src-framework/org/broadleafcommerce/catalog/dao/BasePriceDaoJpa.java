package org.broadleafcommerce.catalog.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.catalog.domain.BasePrice;
import org.springframework.stereotype.Repository;

@Repository("basePriceDao")
public class BasePriceDaoJpa implements BasePriceDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public BasePrice maintainBasePrice(BasePrice basePrice) {
        if (basePrice.getId() == null) {
            em.persist(basePrice);
        } else {
        	basePrice = em.merge(basePrice);
        }
        return basePrice;
    }

    @Override
    public BasePrice readBasePriceById(Long basePriceId) {
        return em.find(BasePrice.class, basePriceId);
    }

}
