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
 * to build the supported fields for a Time entity
 *
 * @author Andre Azzolini (apazzolini)
 */
@Service("blTimeFieldService")
public class TimeFieldServiceImpl extends AbstractRuleBuilderFieldService {

    @Override
    public void init() {
        fields.add(new FieldData.Builder()
                .label("rule_timeHourOfDay")
                .name("hour")
                .operators(RuleOperatorType.SELECTIZE_ENUMERATION)
                .options(RuleOptionType.HOUR_OF_DAY)
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_timeDayOfWeek")
                .name("dayOfWeek")
                .operators(RuleOperatorType.SELECTIZE_ENUMERATION)
                .options(RuleOptionType.DAY_OF_WEEK)
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_timeMonth")
                .name("month")
                .operators(RuleOperatorType.SELECTIZE_ENUMERATION)
                .options(RuleOptionType.MONTH)
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_timeDayOfMonth")
                .name("dayOfMonth")
                .operators(RuleOperatorType.SELECTIZE_ENUMERATION)
                .options(RuleOptionType.DAY_OF_MONTH)
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_timeMinute")
                .name("minute")
                .operators(RuleOperatorType.SELECTIZE_ENUMERATION)
                .options(RuleOptionType.MINUTE)
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_timeDate")
                .name("date")
                .operators(RuleOperatorType.DATE)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.DATE)
                .build());
    }

    @Override
    public String getName() {
        return RuleIdentifier.TIME;
    }

    @Override
    public String getDtoClassName() {
        return "org.broadleafcommerce.common.TimeDTO";
    }
}
