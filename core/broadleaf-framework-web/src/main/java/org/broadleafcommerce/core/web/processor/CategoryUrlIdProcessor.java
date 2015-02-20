/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.processor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.common.security.handler.CsrfFilter;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * Used as a replacement to the HTML {@code <form>} element which adds a CSRF token input field to forms that are submitted
 * via anything but GET. This is required to properly bypass the {@link CsrfFilter}.
 * 
 * @author apazzolini
 * @see {@link CsrfFilter}
 */
@Component("blCategoryUrlIdProcessor")
public class CategoryUrlIdProcessor extends AbstractElementProcessor {
    
    @Resource(name = "blExploitProtectionService")
    protected ExploitProtectionService eps;
    
    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public CategoryUrlIdProcessor() {
        super("categoryurlid");
    }
    
    /**
     * We need this replacement to execute as early as possible to allow subsequent processors to act
     * on this element as if it were a normal form instead of a blc:form
     */
    @Override
    public int getPrecedence() {
        return 1;
    }

    @Override
    protected ProcessorResult processElement(Arguments arguments, Element element) {
        
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue("th:url"));    
        String url = (String) expression.execute(arguments.getConfiguration(), arguments);
        expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue("th:categoryId"));
        Long categoryId = (Long) expression.execute(arguments.getConfiguration(), arguments);
        
        boolean usesCategoryId = BLCSystemProperty.resolveBooleanSystemProperty("category.url.use.id");
        
        if (usesCategoryId && categoryId!=null){
          Map<String, String[]> urlParams = new HashMap<String, String[]>();
          urlParams.put("categoryId", new String[] {Long.toString(categoryId)});
          url = ProcessorUtils.getUrl(url, urlParams);
        }
   
        element.removeAttribute("th:url");
        element.removeAttribute("th:categoryId");
        element.setAttribute("href", url);

        Element newElement = element.cloneElementNodeWithNewName(element.getParent(), "a", false);
        newElement.setRecomputeProcessorsImmediately(true);
        element.getParent().insertAfter(element, newElement);
        element.getParent().removeChild(element);
        
        return ProcessorResult.OK;
    }
    
}
