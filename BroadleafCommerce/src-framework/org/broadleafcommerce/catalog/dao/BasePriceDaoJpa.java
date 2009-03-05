package org.broadleafcommerce.catalog.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.catalog.domain.BasePrice;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("basePriceDao")
public class BasePriceDaoJpa implements BasePriceDao {

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    public BasePrice maintainBasePrice(BasePrice basePrice) {
        if (basePrice.getId() == null) {
            em.persist(basePrice);
        } else {
            basePrice = em.merge(basePrice);
        }
        return basePrice;
    }

    @SuppressWarnings("unchecked")
    public BasePrice readBasePriceById(Long basePriceId) {
        return (BasePrice) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.catalog.domain.BasePrice"), basePriceId);
    }
}
