package org.broadleafcommerce.rules.dao;

import java.util.List;

import org.broadleafcommerce.rules.domain.CouponCode;
import org.broadleafcommerce.rules.domain.PromotionRuleCategory;
import org.broadleafcommerce.rules.domain.ShoppingCartPromotion;

public interface RuleDao {
	
	public List<PromotionRuleCategory> readAllRuleCategories();
	
	public ShoppingCartPromotion maintainShoppingCartPromotion(ShoppingCartPromotion shoppingCartPromotion);
	
	public ShoppingCartPromotion readShoppingCartPromotionById(Long id);

	public List<CouponCode> readAllCouponCodes();
	
}
