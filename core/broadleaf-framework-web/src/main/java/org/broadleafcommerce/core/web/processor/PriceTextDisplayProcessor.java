/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import java.text.NumberFormat;
import java.util.Map;

/**
 * A Thymeleaf processor that renders a Money object according to the currently set locale options.
 * For example, when rendering "6.99" in a US locale, the output text would be "$6.99".
 * When viewing in France for example, you might see "6,99 (US)$". Alternatively, if currency conversion
 * was enabled, you may see "5,59 (euro-symbol)"
 * 
 * @author apazzolini
 */
@Component("blPriceTextDisplayProcessor")
public class PriceTextDisplayProcessor extends AbstractAttributeTagProcessor {
    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public PriceTextDisplayProcessor() {
        super(TemplateMode.HTML, "blc", null, false, "price", true, 1500, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        Money price;
        String result;
        Map<String, String> attributes = tag.getAttributeMap();
        try {
            price = (Money) StandardExpressions.getExpressionParser(context.getConfiguration()).parseExpression(context, attributes.get(attributeName)).execute(context);
        } catch (ClassCastException e) {
            Number value = (Number) StandardExpressions.getExpressionParser(context.getConfiguration()).parseExpression(context, attributes.get(attributeName)).execute(context);
            price = new Money(value.doubleValue());
        }

        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc.getJavaLocale() != null) {
            NumberFormat format = NumberFormat.getCurrencyInstance(brc.getJavaLocale());
            format.setCurrency(price.getCurrency());
            result = format.format(price.getAmount());
        } else {
            // Setup your BLC_CURRENCY and BLC_LOCALE to display a diff default.
            result = "$ " + price.getAmount().toString();
        }
        structureHandler.setBody(result, false);

    }


}
