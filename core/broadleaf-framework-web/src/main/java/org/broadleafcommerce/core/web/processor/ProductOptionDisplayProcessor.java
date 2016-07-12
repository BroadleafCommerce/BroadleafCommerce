/*
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.processor;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.web.dialect.AbstractBroadleafModelVariableModifierProcessor;
import org.broadleafcommerce.common.web.domain.BroadleafThymeleafContext;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Priyesh Patel
 */
@Component("blProductOptionDisplayProcessor")
public class ProductOptionDisplayProcessor extends AbstractBroadleafModelVariableModifierProcessor {

    @Override
    public String getName() {
        return "product_option_display";
    }
    
    @Override
    public int getPrecedence() {
        return 100;
    }
    
    @Override
    public boolean addToLocal() {
        return true;
    }

    protected void initServices(String tagName, Map<String, String> tagAttributes, Map<String, Object> newModelVars) {
        // extending classes can implement this to inject init logic
    }

    @Override
    public void populateModelVariables(String tagName, Map<String, String> tagAttributes, Map<String, Object> newModelVars, BroadleafThymeleafContext context) {
        initServices(tagName, tagAttributes, newModelVars);
        HashMap<String, String> productOptionDisplayValues = new HashMap<String, String>();
        Object item = context.parseExpression(tagAttributes.get("orderItem"));
        if (item instanceof DiscreteOrderItem) {
            DiscreteOrderItem orderItem = (DiscreteOrderItem) item;

            for (String i : orderItem.getOrderItemAttributes().keySet()) {
                for (ProductOption option : orderItem.getProduct().getProductOptions()) {
                    if (option.getAttributeName().equals(i) && !StringUtils.isEmpty(orderItem.getOrderItemAttributes().get(i).toString())) {
                        productOptionDisplayValues.put(option.getLabel(), orderItem.getOrderItemAttributes().get(i).toString());
                    }
                }
            }
        }
        newModelVars.put("productOptionDisplayValues", productOptionDisplayValues);
    }

}
