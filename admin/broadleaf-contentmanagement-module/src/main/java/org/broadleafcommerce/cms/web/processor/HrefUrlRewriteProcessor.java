/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.web.processor;

import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Similar to {@link UrlRewriteProcessor} but handles href tags.   
 * Mainly those that have a useCdn=true attribute or those that are inside a script tag.
 * 
 * @author bpolster
 */
public class HrefUrlRewriteProcessor extends UrlRewriteProcessor {
    
    @Resource(name = "blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;

    private static final String LINK = "link";
    private static final String HREF = "href";

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public HrefUrlRewriteProcessor() {
        super(HREF);
    }

    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        Map<String, String> attrs = new HashMap<String, String>();
        
        String elementName = element.getNormalizedName();
        String useCDN = element.getAttributeValue("useCDN");

        if (LINK.equals(elementName) || (useCDN != null && "true".equals(useCDN))) {
            attrs = super.getModifiedAttributeValues(arguments, element, attributeName);
            String srcAttr = attrs.remove("src");
            attrs.put(HREF, srcAttr);
        } else {
            attrs.put(HREF, element.getAttributeValue(attributeName));
        }
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
