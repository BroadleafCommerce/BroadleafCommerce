/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.web.rulebuilder.service;

import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.presentation.RuleOperatorType;
import org.broadleafcommerce.common.presentation.RuleOptionType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.AbstractRuleBuilderFieldService;
import org.springframework.stereotype.Service;

/**
 * An implementation of a RuleBuilderFieldService
 * that constructs metadata necessary
 * to build the supported fields for a Request entity
 *
 * @author Andre Azzolini (apazzolini)
 */
@Service("blRequestFieldService")
public class RequestFieldServiceImpl extends AbstractRuleBuilderFieldService {

    @Override
    public void init() {
        fields.add(new FieldData.Builder()
                .label("rule_requestSearchKeyword")
                .name("properties['blcSearchKeyword']")
                .operators(RuleOperatorType.TEXT)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.STRING)
                .skipValidation(true)
                .build());

        fields.add(new FieldData.Builder()
                .label("rule_requestFullUrl")
                .name("fullUrlWithQueryString")
                .operators(RuleOperatorType.TEXT)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.STRING)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_requestUri")
                .name("requestURI")
                .operators(RuleOperatorType.TEXT)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.STRING)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_requestIsSecure")
                .name("secure")
                .operators(RuleOperatorType.BOOLEAN)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.BOOLEAN)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_requestDevice")
                .name("properties['currentDevice']")
                .operators(RuleOperatorType.SELECTIZE_ENUMERATION)
                .options(RuleOptionType.WEB_REQUEST_DEVICE_TYPE)
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .skipValidation(true)
                .build());
    }

    @Override
    public String getName() {
        return RuleIdentifier.REQUEST;
    }

    @Override
    public String getDtoClassName() {
        return "org.broadleafcommerce.common.RequestDTOImpl";
    }
}
