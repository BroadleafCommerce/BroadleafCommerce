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

import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.fragment.WholeFragmentSpec;
import org.thymeleaf.processor.element.AbstractFragmentHandlingElementProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.standard.processor.attr.StandardFragmentAttrProcessor;

import java.util.Map;

/**
 * A Thymeleaf processor that will include the standard head element. It will also set the
 * following variables for use by the head fragment.
 * 
 * <ul>
 * 	<li><b>pageTitle</b> - The title of the page</li>
 * 	<li><b>additionalCss</b> - An additional, page specific CSS file to include</li>
 * 	<li><b>metaDescription</b> - Optional, Content for the Meta-Description tag</li>
 * 	<li><b>metaKeywords</b> - Optional, Content for the Meta-Keywords tag</li>
 * 	<li><b>metaRobot</b> - Optional, Content for the Meta-Robots tag</li>
 * </ul>
 * 
 * @author apazzolini
 */
@Component("blHeadProcessor")
public class HeadProcessor extends AbstractFragmentHandlingElementProcessor {

    public static final String FRAGMENT_ATTR_NAME = StandardFragmentAttrProcessor.ATTR_NAME;
    protected String HEAD_PARTIAL_PATH = "layout/partials/head";
    
	/**
	 * Sets the name of this processor to be used in Thymeleaf template
	 */
    public HeadProcessor() {
        super("head");
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

	@Override
	protected boolean getSubstituteInclusionNode(Arguments arguments, Element element) {
		return true;
	}

    @Override
	@SuppressWarnings("unchecked")
    protected FragmentAndTarget getFragmentAndTarget(Arguments arguments, Element element, boolean substituteInclusionNode) {
		// The pageTitle attribute could be an expression that needs to be evaluated. Try to evaluate, but fall back
		// to its text value if the expression wasn't able to be processed. This will allow things like
		// pageTitle="Hello this is a string"
		// as well as expressions like
		// pageTitle="${'Hello this is a ' + product.name}"
		
		String pageTitle = element.getAttributeValue("pageTitle");
		String metaDescription = element.getAttributeValue("metaDescription");
		String metaKeywords = element.getAttributeValue("metaKeywords");
		String metaRobot = element.getAttributeValue("metaRobot");
		try {
			pageTitle = (String) StandardExpressionProcessor.processExpression(arguments, pageTitle);
            if(metaDescription != null){
                metaDescription = (String) StandardExpressionProcessor.processExpression(arguments, metaDescription);
            }
            if(metaKeywords != null){
                metaKeywords = (String) StandardExpressionProcessor.processExpression(arguments, metaKeywords);
            }
            if(metaRobot != null){
                metaRobot = (String) StandardExpressionProcessor.processExpression(arguments, metaRobot);
            }
		} catch (TemplateProcessingException e) {
			// Do nothing.
		}
		((Map<String, Object>) arguments.getExpressionEvaluationRoot()).put("pageTitle", pageTitle);
		((Map<String, Object>) arguments.getExpressionEvaluationRoot()).put("additionalCss", element.getAttributeValue("additionalCss"));
		((Map<String, Object>) arguments.getExpressionEvaluationRoot()).put("metaDescription", metaDescription);
		((Map<String, Object>) arguments.getExpressionEvaluationRoot()).put("metaKeywords", metaKeywords);
		((Map<String, Object>) arguments.getExpressionEvaluationRoot()).put("metaRobot", metaRobot);
		return new FragmentAndTarget(HEAD_PARTIAL_PATH, WholeFragmentSpec.INSTANCE);
    }

}
