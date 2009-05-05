package org.broadleafcommerce.rules.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.rules.domain.ShoppingCartPromotion;
import org.springframework.stereotype.Repository;

@Repository("ruleDao")
public class RuleDaoJpa implements RuleDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName="blPU")
    private EntityManager em;

    @Override
    public ShoppingCartPromotion save(ShoppingCartPromotion shoppingCartPromotion) {
        if (shoppingCartPromotion.getId() == null) {
            em.persist(shoppingCartPromotion);
        } else {
            shoppingCartPromotion = em.merge(shoppingCartPromotion);
        }
        return shoppingCartPromotion;
    }

}
