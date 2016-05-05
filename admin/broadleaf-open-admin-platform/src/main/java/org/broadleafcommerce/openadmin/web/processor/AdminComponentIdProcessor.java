/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
            
            fieldName = "listGrid-" + lg.getListGridType();
            if (StringUtils.isNotBlank(lg.getSubCollectionFieldName())) {
                fieldName += "-" + lg.getSubCollectionFieldName();
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
