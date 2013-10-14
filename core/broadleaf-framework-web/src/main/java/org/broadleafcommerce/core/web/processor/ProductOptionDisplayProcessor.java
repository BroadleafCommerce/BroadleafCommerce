/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.processor;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.element.AbstractLocalVariableDefinitionElementProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

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
        if (StandardExpressionProcessor.processExpression(arguments,
                element.getAttributeValue("orderItem")) instanceof DiscreteOrderItem) {

            DiscreteOrderItem orderItem = (DiscreteOrderItem) StandardExpressionProcessor.processExpression(arguments,
                    element.getAttributeValue("orderItem"));

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
