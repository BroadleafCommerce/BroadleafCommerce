/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.processor;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.HashMap;
import java.util.Map;

/**
 * A Thymeleaf processor that will generate the appropriate ID for a given admin component.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Component("blAdminComponentIdProcessor")
public class AdminComponentIdProcessor extends AbstractAttributeModifierAttrProcessor {

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public AdminComponentIdProcessor() {
        super("component_id");
    }

    @Override
    public int getPrecedence() {
        return 10002;
    }

    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue(attributeName));
        Object component = expression.execute(arguments.getConfiguration(), arguments);

        String fieldName = "";
        String id = "";
        
        if (component instanceof ListGrid) {
            ListGrid lg = (ListGrid) component;
            
            fieldName = "listGrid-";
            if (ListGrid.Type.MAIN.toString().toLowerCase().equals(lg.getListGridType())) {
                fieldName += ListGrid.Type.MAIN.toString().toLowerCase();
            } else {
                fieldName = fieldName + lg.getListGridType() + '-' + lg.getSubCollectionFieldName();
            }
        } else if (component instanceof Field) {
            Field field = (Field) component;
            fieldName = "field-" + field.getName();
        }
        
        if (StringUtils.isNotBlank(fieldName)) {
            id = cleanCssIdString(fieldName);
        }
        
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put("id", id);
        return attrs;
    }

    protected String cleanCssIdString(String in) {
        in = in.replaceAll("[^a-zA-Z0-9-]", "-");
        return in;
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
