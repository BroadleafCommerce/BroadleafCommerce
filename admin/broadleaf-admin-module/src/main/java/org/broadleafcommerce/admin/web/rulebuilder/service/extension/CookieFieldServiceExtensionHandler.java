/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.admin.web.rulebuilder.service.extension;

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.core.rule.RuleDTOConfig;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.AbstractRuleBuilderFieldServiceExtensionHandler;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldServiceExtensionManager;

import java.util.List;

import javax.annotation.PostConstruct;

/**
 * Add configured cookies as fields to request-based rule builder.
 * </p>
 * Configuration is generally as easy as enabling the feature
 * via a property and then configuring one or more cookie configurations.
 * </p>
 * Add {@code cookie.content.targeting.enabled=true} to a property file visible to both admin and site (i.e. common-shared.properties)
 * </p>
 * Add a cookie configuration to your Spring xml or Java configuration. Sample below demonstrated Java-based config:
 * {@code
 *    @Merge("blCookieRuleConfigs")
 *    public RuleDTOConfig myCookieRuleDTOConfig() {
 *        RuleDTOConfig config = new RuleDTOConfig("myFieldName", "myLabel");
 *        config.setAlternateName("cookieName");
 *        return config;
 *    }
 * }
 *
 * @author Jeff Fischer
 */
public class CookieFieldServiceExtensionHandler extends AbstractRuleBuilderFieldServiceExtensionHandler {

    public static final String COOKIE_ATTRIBUTE_NAME = "_blCookieAttribute";

    protected RuleBuilderFieldServiceExtensionManager extensionManager;
    protected List<RuleDTOConfig> fieldConfigs;

    public CookieFieldServiceExtensionHandler(RuleBuilderFieldServiceExtensionManager extensionManager, List<RuleDTOConfig> fieldConfigs) {
        this.extensionManager = extensionManager;
        this.fieldConfigs = fieldConfigs;
    }

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType addFields(List<FieldData> fields, String ruleFieldName, String dtoClassName) {
        if (RuleIdentifier.REQUEST.equals(ruleFieldName)) {
            for (RuleDTOConfig fieldConfig : fieldConfigs) {
                fields.add(new FieldData.Builder()
                    .label(fieldConfig.getLabel())
                    .name(fieldConfig.getFieldName())
                    .operators(fieldConfig.getOperators())
                    .options(fieldConfig.getOptions())
                    .type(fieldConfig.getType())
                    .skipValidation(true)
                    .overrideEntityKey(COOKIE_ATTRIBUTE_NAME)
                    .build());
            }
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
