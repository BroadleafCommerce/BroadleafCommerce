/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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

package org.broadleafcommerce.core.web.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * Default implementation of {@link TemplateCacheKeyResolverService} that returns a concatenation of a 
 * templateName and cacheKey.   If the cacheKey is set to none, null is returned resulting in no cache.
 * 
 * @author Brian Polster (bpolster)
 */
@Service("blTemplateCacheKeyResolver")
public class SimpleCacheKeyResolver implements TemplateCacheKeyResolverService {
    
    /**
     * Returns a concatenation of the templateName and cacheKey separated by an "_".    
     * If cacheKey is null, only the templateName is returned.
     * 
     * If cacheKey is "none" then null will be returned causing the template not to be cached.
     * 
     * @param templateName - Name of the template that is subject to being cached. 
     * @param cacheKey - Value of the parameter passed in from the template
     * @return
     */
    public String resolveCacheKey(Arguments arguments, Element element, String templateName) {
        String cacheKeyParam = "";
        String cacheKeyAttrValue = element.getAttributeValueFromNormalizedName("cachekey");

        if (cacheKeyAttrValue != null) {
            Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                    .parseExpression(arguments.getConfiguration(), arguments, cacheKeyAttrValue);
            cacheKeyParam = (String) expression.execute(arguments.getConfiguration(), arguments);
        }

        if (cacheKeyParam != null) {
            if ("none".equals(cacheKeyParam)) {
                return null;
            }
            return templateName + "_" + cacheKeyParam;
        } else {
            return templateName;
        }
    }
}
