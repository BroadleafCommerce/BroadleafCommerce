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

import org.apache.commons.lang.ArrayUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;
import org.broadleafcommerce.core.web.service.SearchFacetDTOService;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * A Thymeleaf processor that processes the value attribute on the element it's tied to
 * with a predetermined value based on the SearchFacetResultDTO object that is passed into this
 * processor. 
 * 
 * @author apazzolini
 */
public class ToggleFacetLinkProcessor extends AbstractAttributeModifierAttrProcessor {
    
    @Resource(name = "blSearchFacetDTOService")
    protected SearchFacetDTOService facetService;

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public ToggleFacetLinkProcessor() {
        super("togglefacetlink");
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        Map<String, String> attrs = new HashMap<String, String>();
        
        BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
        HttpServletRequest request = blcContext.getRequest();
        
        String baseUrl = request.getRequestURL().toString();
        Map<String, String[]> params = new HashMap<String, String[]>(request.getParameterMap());
        
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue(attributeName));
        SearchFacetResultDTO result = (SearchFacetResultDTO) expression.execute(arguments.getConfiguration(), arguments);
        
        String key = facetService.getUrlKey(result);
        String value = facetService.getValue(result);
        String[] paramValues = params.get(key);
        
        if (ArrayUtils.contains(paramValues, facetService.getValue(result))) {
            paramValues = (String[]) ArrayUtils.removeElement(paramValues, facetService.getValue(result));
        } else {
            paramValues = (String[]) ArrayUtils.add(paramValues, value);
        }
        
        params.remove(SearchCriteria.PAGE_NUMBER);
        params.put(key, paramValues);
        
        String url = ProcessorUtils.getUrl(baseUrl, params);
        
        attrs.put("href", url);
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
