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

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * A Thymeleaf processor that processes the value attribute on the element it's tied to
 * with a predetermined value based on the SearchFacetResultDTO object that is passed into this
 * processor. 
 * 
 * @author apazzolini
 */
@Component("blPaginationPageLinkProcessor")
public class PaginationPageLinkProcessor extends AbstractAttributeTagProcessor {

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public PaginationPageLinkProcessor() {
        super(TemplateMode.HTML, "blc", null, false, "paginationpagelink", true, 10000, true);
    }
    


    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        Map<String, String> attrs = new HashMap<String, String>();

        BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
        HttpServletRequest request = blcContext.getRequest();

        String baseUrl = request.getRequestURL().toString();
        Map<String, String[]> params = new HashMap<String, String[]>(request.getParameterMap());

        Integer page = (Integer) StandardExpressions.getExpressionParser(context.getConfiguration()).parseExpression( context, attributeValue).execute(context);
        if (page != null && page > 1) {
            params.put(ProductSearchCriteria.PAGE_NUMBER, new String[] { page.toString() });
        } else {
            params.remove(ProductSearchCriteria.PAGE_NUMBER);
        }

        String url = ProcessorUtils.getUrl(baseUrl, params);

        attrs.put("href", url);
        structureHandler.setAttribute("href", url, AttributeValueQuotes.DOUBLE);
    }
}
