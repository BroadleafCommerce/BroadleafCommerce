/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.web.rulebuilder.service;

import org.broadleafcommerce.common.presentation.RuleIdentifier;
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
                .operators("blcOperators_Enumeration")
                .options("blcOptions_HourOfDay")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_timeDayOfWeek")
                .name("dayOfWeek")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_DayOfWeek")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_timeMonth")
                .name("month")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_Month")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_timeDayOfMonth")
                .name("dayOfMonth")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_DayOfMonth")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_timeMinute")
                .name("minute")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_Minute")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        
        fields.add(new FieldData.Builder()
                .label("rule_timeDate")
                .name("date")
                .operators("blcOperators_Date")
                .options("[]")
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
