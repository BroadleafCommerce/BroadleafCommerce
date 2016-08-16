/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package org.broadleafcommerce.core.web.expression;

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogURLService;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * Exposes "blc" to expressions to the Thymeleaf expression context.
 * 
 * This class is intended to be augmented using load time weaving by other modules
 * within Broadleaf.
 * 
 * It provides one function (getDate()) primarily just for testing purposes.   This can
 * be accessed with Thymeleaf as ${#blc.date()}
 * 
 * @author bpolster
 */
public class BLCVariableExpression implements BroadleafVariableExpression {
    
    @Override
    public String getName() {
        return "blc";
    }
    
    @Resource(name = "blCatalogURLService")
    protected CatalogURLService catalogURLService;

    public String relativeURL(Category category) {
        return catalogURLService.buildRelativeCategoryURL(getCurrentUrl(), category);
    }

    public String relativeURL(Product product) {
        return catalogURLService.buildRelativeProductURL(getCurrentUrl(), product);
    }

    public String relativeURL(String baseUrl, Category category) {
        return catalogURLService.buildRelativeCategoryURL(baseUrl, category);
    }

    public String relativeURL(String baseUrl, Product product) {
        return catalogURLService.buildRelativeProductURL(baseUrl, product);
    }

    protected String getCurrentUrl() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        String currentUrl = "";
        if (brc != null && brc.getRequest() != null) {
            currentUrl = brc.getRequest().getRequestURI();

            if (!StringUtils.isEmpty(brc.getRequest().getQueryString())) {
                currentUrl = currentUrl + "?" + brc.getRequest().getQueryString();
            }
        }
        return currentUrl;
    }

    /**
     * Returns the price at the correct scale and rounding for the default currency
     * @see Money#defaultCurrency()
     * @param amount
     * @return
     */
    public String getPrice(String amount) {
        Money price = Money.ZERO;
        String sanitizedAmount = StringUtil.removeNonNumerics(amount);
        if(StringUtils.isEmpty(amount)) {
            Double rawValue = Double.parseDouble(sanitizedAmount);
            BigDecimal value = new BigDecimal(rawValue);
            price = BroadleafCurrencyUtils.getMoney(value);
        }
        String priceString = price.getAmount().toString();
        return priceString;
    }
}
