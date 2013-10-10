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

package org.broadleafcommerce.cms.web.processor;

import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * A Thymeleaf processor that processes the given url through the StaticAssetService's
 * {@link StaticAssetService#convertAssetPath(String, String, boolean)} method to determine
 * the appropriate URL for the asset to be served from.
 * 
 * @author apazzolini
 */
@Component("blUrlRewriteProcessor")
public class UrlRewriteProcessor extends AbstractAttributeModifierAttrProcessor {
    
    @Resource(name = "blStaticAssetService")
    protected StaticAssetService staticAssetService;

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public UrlRewriteProcessor() {
        super("src");
    }
    
    @Override
    public int getPrecedence() {
        return 1000;
    }
    
    /**
     * @return true if the current request.scheme = HTTPS or if the request.isSecure value is true.
     */
    protected boolean isRequestSecure(HttpServletRequest request) {
        return ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
    } 

    
    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        Map<String, String> attrs = new HashMap<String, String>();
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        
        boolean secureRequest = isRequestSecure(request);
        String assetPath = (String) StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue(attributeName));
        //String assetPath = element.getAttributeValue(attributeName);
        
        assetPath = staticAssetService.convertAssetPath(assetPath, request.getContextPath(), secureRequest);
        
        attrs.put("src", assetPath);
        
        
        /*
        SearchFacetResultDTO result = (SearchFacetResultDTO) StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue(attributeName));
        String value = result.getFacet().getSearchFacet().getFieldName() + "[RESULT-VALUE]";
        if (result.getValue() != null) {
            value = value.replace("RESULT-VALUE", result.getValue());
        } else {
            value = value.replace("RESULT-VALUE", result.getMinValue() + "-" + result.getMaxValue());
        }
        */
        
        /*
        attrs.put("id", value);
        attrs.put("name", value);
        */
        
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
