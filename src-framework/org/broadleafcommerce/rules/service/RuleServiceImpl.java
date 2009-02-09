package org.broadleafcommerce.rules.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.rules.dao.RuleDao;
import org.broadleafcommerce.rules.domain.PromotionRuleCategory;
import org.broadleafcommerce.rules.domain.ShoppingCartPromotion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("ruleService")
public class RuleServiceImpl implements RuleService {

	@Resource
	private RuleDao ruleDao;
	
	@Override
	public List<PromotionRuleCategory> readAllRuleCategories() {
		return ruleDao.readAllRuleCategories();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public ShoppingCartPromotion saveShoppingCartPromotion(ShoppingCartPromotion shoppingCartPromotion) {
		return ruleDao.maintainShoppingCartPromotion(shoppingCartPromotion);
	}

	@Override
	public ShoppingCartPromotion readShoppingCartPromotionById(Long id) {
		return ruleDao.readShoppingCartPromotionById(id);
	}

}
