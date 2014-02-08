/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.processor;

import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;


/**
 * A Thymeleaf processor that will lookup the value of a configuration variable.
 * Takes in a configuration variable attribute name and returns the value. 
 * 
 * @author bpolster
 */
public class ConfigVariableProcessor extends AbstractModelVariableModifierProcessor {

    public ConfigVariableProcessor() {
        super("config");
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        String resultVar = element.getAttributeValue("resultVar");
        if (resultVar == null) {
            resultVar = "value";
        }
        
        String attributeName = element.getAttributeValue("name");
        String attributeValue = BLCSystemProperty.resolveSystemProperty(attributeName);
        
        addToModel(arguments, resultVar, attributeValue);
    }
}
