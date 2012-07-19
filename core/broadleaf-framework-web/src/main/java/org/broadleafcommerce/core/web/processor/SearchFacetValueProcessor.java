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

import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * A Thymeleaf processor that processes the value attribute on the element it's tied to
 * with a predetermined value based on the SearchFacetResultDTO object that is passed into this
 * processor. 
 * 
 * @author apazzolini
 */
public class SearchFacetValueProcessor extends AbstractAttributeModifierAttrProcessor {

	/**
	 * Sets the name of this processor to be used in Thymeleaf template
	 */
	public SearchFacetValueProcessor() {
		super("facetvalue");
	}
	
	@Override
	public int getPrecedence() {
		return 10000;
	}

	@Override
	protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
		Map<String, String> attrs = new HashMap<String, String>();
		
		SearchFacetResultDTO result = (SearchFacetResultDTO) StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue(attributeName));
		String value = result.getFacet().getSearchFacet().getFieldName() + "[RESULT-VALUE]";
		if (result.getValue() != null) {
			value = value.replace("RESULT-VALUE", result.getValue());
		} else {
			value = value.replace("RESULT-VALUE", result.getMinValue() + "-" + result.getMaxValue());
		}
		
		attrs.put("id", value);
		attrs.put("name", value);
		
		return attrs;
	}

	@Override
	protected ModificationType getModificationType(Arguments arguments, Element element, String attributeName, String newAttributeName) {
		return ModificationType.SUBSTITUTION;
	}

	@Override
	protected boolean removeAttributeIfEmpty(Arguments arguments, Element element, String attributeName, String newAttributeName) {
		return true;
	}

	@Override
	protected boolean recomputeProcessorsAfterExecution(Arguments arguments, Element element, String attributeName) {
		return false;
	}
}
