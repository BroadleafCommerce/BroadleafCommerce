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

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.server.service.type.RuleIdentifier;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.AbstractRuleBuilderFieldService;
import org.springframework.stereotype.Service;

/**
 * An implementation of a RuleBuilderFieldService
 * that constructs metadata necessary
 * to build the supported fields for a Product entity
 *
 * @author Andre Azzolini (apazzolini)
 */
@Service("blProductFieldService")
public class ProductFieldServiceImpl extends AbstractRuleBuilderFieldService {

    public void init() {
        fields.add(new FieldData.Builder()
                .label("Product - URL")
                .name("url")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("Product - URLKey")
                .name("urlKey")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("Product - Is Featured Product")
                .name("isFeaturedProduct")
                .operators("blcOperators_Boolean")
                .options("[]")
                .type(SupportedFieldType.BOOLEAN)
                .build());
        fields.add(new FieldData.Builder()
                .label("Product - Manufacturer")
                .name("manufacturer")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("Product - Model")
                .name("model")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("Sku - Name")
                .name("defaultSku.name")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("Sku - Fulfillment Type")
                .name("defaultSku.fulfillmentType")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_FulfillmentType")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        fields.add(new FieldData.Builder()
                .label("Sku - Inventory Type")
                .name("defaultSku.inventoryType")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_InventoryType")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        fields.add(new FieldData.Builder()
                .label("Sku - Description")
                .name("defaultSku.description")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("Sku - Long Description")
                .name("defaultSku.longDescription")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("Sku - Taxable")
                .name("defaultSku.taxable")
                .operators("blcOperators_Boolean")
                .options("[]")
                .type(SupportedFieldType.BOOLEAN)
                .build());
        fields.add(new FieldData.Builder()
                .label("Sku - Available")
                .name("defaultSku.available")
                .operators("blcOperators_Boolean")
                .options("[]")
                .type(SupportedFieldType.BOOLEAN)
                .build());
        fields.add(new FieldData.Builder()
                .label("Sku - Start Date")
                .name("defaultSku.activeStartDate")
                .operators("blcOperators_Date")
                .options("[]")
                .type(SupportedFieldType.DATE)
                .build());
        fields.add(new FieldData.Builder()
                .label("Sku - End Date")
                .name("defaultSku.activeEndDate")
                .operators("blcOperators_Date")
                .options("[]")
                .type(SupportedFieldType.DATE)
                .build());
    }

    @Override
    public String getName() {
        return RuleIdentifier.PRODUCT;
    }

    @Override
    public String getDtoClassName() {
        return "org.broadleafcommerce.core.catalog.domain.ProductImpl";
    }
}
