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

import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldService;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldServiceFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.element.AbstractLocalVariableDefinitionElementProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blAdminFieldBuilderProcessor")
public class AdminFieldBuilderProcessor extends AbstractLocalVariableDefinitionElementProcessor {

    @Resource(name = "blRuleBuilderFieldServiceFactory")
    protected RuleBuilderFieldServiceFactory ruleBuilderFieldServiceFactory;

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public AdminFieldBuilderProcessor() {
        super("admin_field_builder");
    }

    @Override
    public int getPrecedence() {
        return 100;
    }

    @Override
    protected Map<String, Object> getNewLocalVariables(Arguments arguments, Element element) {
        FieldWrapper fieldWrapper = new FieldWrapper();
        
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue("fieldBuilder"));
        String fieldBuilder = (String) expression.execute(arguments.getConfiguration(), arguments);

        if (fieldBuilder != null) {
            RuleBuilderFieldService ruleBuilderFieldService = ruleBuilderFieldServiceFactory.createInstance(fieldBuilder);
            if (ruleBuilderFieldService != null) {
                fieldWrapper = ruleBuilderFieldService.buildFields();
            }
        }
        
        Map<String, Object> newVars = new HashMap<String, Object>();
        newVars.put("fieldWrapper", fieldWrapper);
        return newVars;
    }

    @Override
    protected boolean removeHostElement(Arguments arguments, Element element) {
        return false;
    }
}
