/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blAdminFieldBuilderProcessor")
@ConditionalOnTemplating
public class AdminFieldBuilderProcessor implements AdminFieldBuilderExpression {

    @Resource(name = "blRuleBuilderFieldServiceFactory")
    protected RuleBuilderFieldServiceFactory ruleBuilderFieldServiceFactory;

    @Resource(name = "blAdminFieldBuilderProcessorExtensionManager")
    protected AdminFieldBuilderProcessorExtensionManager extensionManager;

    @Override
    public String getName() {
        return "admin_field_builder";
    }

    @Override
    public int getPrecedence() {
        return 100;
    }

    @Override
    public FieldWrapper getFieldWrapper(String fieldBuilder, String ceilingEntity) {
        FieldWrapper fieldWrapper = new FieldWrapper();

        if (fieldBuilder != null) {
            RuleBuilderFieldService ruleBuilderFieldService = ruleBuilderFieldServiceFactory.createInstance(fieldBuilder);
            if (ruleBuilderFieldService != null) {
                fieldWrapper = ruleBuilderFieldService.buildFields();
            }
        }

        if (extensionManager != null) {
            extensionManager.getProxy().modifyRuleBuilderFields(fieldBuilder, ceilingEntity, fieldWrapper);
        }

        return fieldWrapper;
    }

    @Override
    public boolean useGlobalScope() {
        return false;
    }

}
