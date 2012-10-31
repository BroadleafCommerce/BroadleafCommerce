/*
 * Copyright 2012 the original author or authors.
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
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import java.math.BigDecimal;

/**
 * A Thymeleaf processor that renders a Money object according to the currently set locale options.
 * For example, when rendering "6.99" in a US locale, the output text would be "$6.99".
 * When viewing in France for example, you might see "6,99 (US)$". Alternatively, if currency conversion
 * was enabled, you may see "5,59 (euro-symbol)"
 * 
 * @author apazzolini
 */
@Component("blPriceTextDisplayProcessor")
public class PriceTextDisplayProcessor extends AbstractTextChildModifierAttrProcessor {

	/**
	 * Sets the name of this processor to be used in Thymeleaf template
	 */
	public PriceTextDisplayProcessor() {
		super("price");
	}
	
	@Override
	public int getPrecedence() {
		return 1500;
	}

	// TODO: Actually make the returned text formatted for the given locale
	@Override
	protected String getText(Arguments arguments, Element element, String attributeName) {
		Money price;
		try {
			price = (Money) StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue(attributeName));
		} catch (ClassCastException e) {
			BigDecimal value = (BigDecimal) StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue(attributeName));
			price = new Money(value);
		}
		
		if (price == null || price.isZero()) {
			return "$0.00";
		} else {
			return "$" + price.getAmount().toString();
		}
	}
}
