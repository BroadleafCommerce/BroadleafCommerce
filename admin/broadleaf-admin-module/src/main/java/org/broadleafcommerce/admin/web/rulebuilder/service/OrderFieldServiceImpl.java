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
 * to build the supported fields for an Order entity
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blOrderFieldService")
public class OrderFieldServiceImpl extends AbstractRuleBuilderFieldService {

    @Override
    public void init() {
        fields.add(new FieldData.Builder()
                .label("rule_orderCurrenceIsDefault")
                .name("currency.defaultFlag")
                .operators("blcOperators_Boolean")
                .options("[]")
                .type(SupportedFieldType.BOOLEAN)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderCurrencyCode")
                .name("currency.currencyCode")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderCurrencyName")
                .name("currency.friendlyName")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderLocaleIsDefault")
                .name("locale.defaultFlag")
                .operators("blcOperators_Boolean")
                .options("[]")
                .type(SupportedFieldType.BOOLEAN)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderLocaleCode")
                .name("locale.localeCode")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderLocaleName")
                .name("locale.friendlyName")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderSubtotal")
                .name("subTotal")
                .operators("blcOperators_Numeric")
                .options("[]")
                .type(SupportedFieldType.MONEY)
                .build());
    }

    @Override
    public String getName() {
        return RuleIdentifier.ORDER;
    }

    @Override
    public String getDtoClassName() {
        return "org.broadleafcommerce.core.order.domain.OrderImpl";
    }
}
