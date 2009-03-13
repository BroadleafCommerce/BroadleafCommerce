package org.broadleafcommerce.rules.service;

import java.io.File;

import org.broadleafcommerce.rules.domain.ShoppingCartPromotion;
import org.drools.rule.Package;

public interface RuleService {

	public ShoppingCartPromotion saveShoppingCartPromotion(ShoppingCartPromotion shoppingCartPromotion);

	public Package addRuleToNewPackage(File drlFile);

	public void mergePackageWithRuleBase(Package pkg);

	public void writeRuleFile(ShoppingCartPromotion shoppingCartPromotion, String logicalOperator);

}
