package org.broadleafcommerce.rules.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.rules.domain.CouponCode;
import org.broadleafcommerce.rules.domain.PromotionRuleCategory;
import org.broadleafcommerce.rules.domain.ShoppingCartPromotion;
import org.springframework.stereotype.Repository;

@Repository("ruleDao")
public class RuleDaoJpa implements RuleDao {

	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<PromotionRuleCategory> readAllRuleCategories() {
		Query query = em.createNamedQuery("READ_ALL_RULE_CATEGORIES");
		List<PromotionRuleCategory> ruleCategories = query.getResultList();
		return ruleCategories;
	}

    @Override
    public ShoppingCartPromotion maintainShoppingCartPromotion(ShoppingCartPromotion shoppingCartPromotion) {
        if (shoppingCartPromotion.getId() == null) {
        	Date now = new Date();
        	shoppingCartPromotion.setCreated(now);
        	shoppingCartPromotion.setModified(now);
            em.persist(shoppingCartPromotion);
        } else {
        	shoppingCartPromotion.setModified(new Date());
        	shoppingCartPromotion = em.merge(shoppingCartPromotion);
        }
        return shoppingCartPromotion;
    }

	@Override
	public ShoppingCartPromotion readShoppingCartPromotionById(Long id) {
		Query query = em.createNamedQuery("READ_PROMOTION_RULE_BY_ID");
		query.setParameter("id", id);
		ShoppingCartPromotion shoppingCartPromotion = (ShoppingCartPromotion) query.getSingleResult();
		return shoppingCartPromotion;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CouponCode> readAllCouponCodes() {
		Query query = em.createNamedQuery("READ_ALL_COUPON_CODES");
		return query.getResultList();
	}
}
