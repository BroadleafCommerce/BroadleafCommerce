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
package org.broadleafcommerce.openadmin.processor;

import org.broadleafcommerce.common.web.condition.TemplatingExistCondition;
import org.broadleafcommerce.common.web.dialect.AbstractBroadleafModelVariableModifierProcessor;
import org.broadleafcommerce.common.web.dialect.BroadleafDialectPrefix;
import org.broadleafcommerce.common.web.domain.BroadleafTemplateContext;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldService;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldServiceFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blAdminFieldBuilderProcessor")
@Conditional(TemplatingExistCondition.class)
public class AdminFieldBuilderProcessor extends AbstractBroadleafModelVariableModifierProcessor {

    @Resource(name = "blRuleBuilderFieldServiceFactory")
    protected RuleBuilderFieldServiceFactory ruleBuilderFieldServiceFactory;

    @Override
    public String getName() {
        return "admin_field_builder";
    }
    
    @Override
    public String getPrefix() {
        return BroadleafDialectPrefix.BLC_ADMIN;
    }
    
    @Override
    public int getPrecedence() {
        return 100;
    }

    @Override
    public void populateModelVariables(String tagName, Map<String, String> tagAttributes, Map<String, Object> newModelVars, BroadleafTemplateContext context) {
        FieldWrapper fieldWrapper = new FieldWrapper();
        String fieldBuilder = (String) context.parseExpression(tagAttributes.get("fieldBuilder"));

        if (fieldBuilder != null) {
            RuleBuilderFieldService ruleBuilderFieldService = ruleBuilderFieldServiceFactory.createInstance(fieldBuilder);
            if (ruleBuilderFieldService != null) {
                fieldWrapper = ruleBuilderFieldService.buildFields();
            }
        }
        
        newModelVars.put("fieldWrapper", fieldWrapper);
    }
    
    public boolean addToLocal() {
        return true;
    }


}
