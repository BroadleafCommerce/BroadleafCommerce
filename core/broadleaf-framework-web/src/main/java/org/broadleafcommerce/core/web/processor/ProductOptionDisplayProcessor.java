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
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Priyesh Patel
 */
@Component("blProductOptionDisplayProcessor")
public class ProductOptionDisplayProcessor extends AbstractElementTagProcessor {

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public ProductOptionDisplayProcessor() {
        super(TemplateMode.HTML, "blc", "product_option_display", true, null, false, 10000);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        HashMap<String, String> productOptionDisplayValues = new HashMap<String, String>();
        Map<String, String> attributeMap = tag.getAttributeMap();
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        Object item = expressionParser.parseExpression(context, attributeMap.get("orderItem")).execute(context);
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
        structureHandler.setLocalVariable("productOptionDisplayValues", productOptionDisplayValues);

    }
}
