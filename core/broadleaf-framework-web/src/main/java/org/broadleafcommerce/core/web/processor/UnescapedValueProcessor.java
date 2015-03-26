/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;


public class UnescapedValueProcessor extends AbstractTextChildModifierAttrProcessor {

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public UnescapedValueProcessor() {
        super("uvalue");
    }
    
    @Override
    public int getPrecedence() {
        return 1500;
    }

    @Override
    protected String getText(Arguments arguments, Element element, String attributeName) {
        
        element.setAttribute("value", element.getAttributeValue(attributeName));
        return null;
    }
}
