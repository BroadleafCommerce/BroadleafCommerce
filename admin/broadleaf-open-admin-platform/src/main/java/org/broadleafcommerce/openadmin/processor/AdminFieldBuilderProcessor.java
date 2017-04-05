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

import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldWrapper;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldService;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldServiceFactory;
import org.broadleafcommerce.openadmin.web.service.AdminFieldBuilderProcessorExtensionManager;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafVariableModifierProcessor;
import org.broadleafcommerce.presentation.dialect.BroadleafDialectPrefix;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blAdminFieldBuilderProcessor")
@ConditionalOnTemplating
public class AdminFieldBuilderProcessor extends AbstractBroadleafVariableModifierProcessor {

    @Resource(name = "blRuleBuilderFieldServiceFactory")
    protected RuleBuilderFieldServiceFactory ruleBuilderFieldServiceFactory;
    
    @Resource(name="blAdminFieldBuilderProcessorExtensionManager")
    protected AdminFieldBuilderProcessorExtensionManager extensionManager;

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
    public Map<String, Object> populateModelVariables(String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        FieldWrapper fieldWrapper = new FieldWrapper();
        String fieldBuilder = context.parseExpression(tagAttributes.get("fieldBuilder"));
        String ceilingEntity = context.parseExpression(tagAttributes.get("ceilingEntity"));

        if (fieldBuilder != null) {
            RuleBuilderFieldService ruleBuilderFieldService = ruleBuilderFieldServiceFactory.createInstance(fieldBuilder);
            if (ruleBuilderFieldService != null) {
                fieldWrapper = ruleBuilderFieldService.buildFields();
            }
        }
        
        if (extensionManager != null) {
            extensionManager.getProxy().modifyRuleBuilderFields(fieldBuilder, ceilingEntity, fieldWrapper);
        }
        
        return ImmutableMap.of("fieldWrapper", (Object) fieldWrapper);
    }
    
    @Override
    public boolean useGlobalScope() {
        return false;
    }


}
