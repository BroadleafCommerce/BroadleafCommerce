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
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.element.AbstractLocalVariableDefinitionElementProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Priyesh Patel
 */
public class ProductOptionDisplayProcessor extends AbstractLocalVariableDefinitionElementProcessor {

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public ProductOptionDisplayProcessor() {
        super("product_option_display");
    }

    @Override
    public int getPrecedence() {
        return 100;
    }

    protected void initServices(Arguments arguments) {

    }

    @Override
    protected Map<String, Object> getNewLocalVariables(Arguments arguments, Element element) {
        initServices(arguments);
        HashMap<String, String> productOptionDisplayValues = new HashMap<String, String>();
        Map<String, Object> newVars = new HashMap<String, Object>();
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue("orderItem"));
        Object item = expression.execute(arguments.getConfiguration(), arguments);
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
        newVars.put("productOptionDisplayValues", productOptionDisplayValues);

        return newVars;
    }

    @Override
    protected boolean removeHostElement(Arguments arguments, Element element) {
        return false;
    }
}
