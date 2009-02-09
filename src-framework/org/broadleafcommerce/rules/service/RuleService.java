package org.broadleafcommerce.rules.service;

import java.util.List;

import org.broadleafcommerce.rules.domain.PromotionRuleCategory;
import org.broadleafcommerce.rules.domain.ShoppingCartPromotion;

public interface RuleService {

	public List<PromotionRuleCategory> readAllRuleCategories();
	
	public ShoppingCartPromotion saveShoppingCartPromotion(ShoppingCartPromotion shoppingCartPromotion);
	
	public ShoppingCartPromotion readShoppingCartPromotionById(Long id);
	
}
