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
 * to build the supported fields for an Order Item entity
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blOrderItemFieldService")
public class OrderItemFieldServiceImpl extends AbstractRuleBuilderFieldService {

    //TODO: extensibility mechanism, support i18N
    @Override
    public void init() {
        fields.add(new FieldData.Builder()
                .label("rule_orderItemName")
                .name("name")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemPrice")
                .name("price")
                .operators("blcOperators_Numeric")
                .options("[]")
                .type(SupportedFieldType.MONEY)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemQuantity")
                .name("quantity")
                .operators("blcOperators_Numeric")
                .options("[]")
                .type(SupportedFieldType.INTEGER)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemRetailPrice")
                .name("retailPrice")
                .operators("blcOperators_Numeric")
                .options("[]")
                .type(SupportedFieldType.MONEY)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemSalePrice")
                .name("salePrice")
                .operators("blcOperators_Numeric")
                .options("[]")
                .type(SupportedFieldType.MONEY)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemCategoryId")
                .name("category.id")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.ID)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemCategoryName")
                .name("category.name")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemCategoryFulfillmentType")
                .name("category.fulfillmentType")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_FulfillmentType")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemInventoryType")
                .name("category.inventoryType")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_InventoryType")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemCategoryUrl")
                .name("category.url")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemCategoryUrlKey")
                .name("category.urlKey")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemCategoryDescription")
                .name("category.description")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemCategoryLongDescription")
                .name("category.longDescription")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemProductCanSellWithoutOptions")
                .name("product.canSellWithoutOptions")
                .operators("blcOperators_Boolean")
                .options("[]")
                .type(SupportedFieldType.BOOLEAN)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemProductUrl")
                .name("product.url")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemProductUrlKey")
                .name("product.urlKey")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemProductIsFeatured")
                .name("product.isFeaturedProduct")
                .operators("blcOperators_Boolean")
                .options("[]")
                .type(SupportedFieldType.BOOLEAN)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemProductManufacturer")
                .name("product.manufacturer")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemProductModel")
                .name("product.model")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemSkuFulfillmentType")
                .name("sku.fulfillmentType")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_FulfillmentType")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemSkuInventoryType")
                .name("sku.inventoryType")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_InventoryType")
                .type(SupportedFieldType.BROADLEAF_ENUMERATION)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemSkuDescription")
                .name("sku.description")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemSkuLongDescription")
                .name("sku.longDescription")
                .operators("blcOperators_Text")
                .options("[]")
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemSkuTaxable")
                .name("sku.taxable")
                .operators("blcOperators_Boolean")
                .options("[]")
                .type(SupportedFieldType.BOOLEAN)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemSkuAvailable")
                .name("sku.available")
                .operators("blcOperators_Boolean")
                .options("[]")
                .type(SupportedFieldType.BOOLEAN)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemSkuStartDate")
                .name("sku.activeStartDate")
                .operators("blcOperators_Date")
                .options("[]")
                .type(SupportedFieldType.DATE)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemSkuEndDate")
                .name("sku.activeEndDate")
                .operators("blcOperators_Date")
                .options("[]")
                .type(SupportedFieldType.DATE)
                .build());
    }

    @Override
    public String getName() {
        return RuleIdentifier.ORDERITEM;
    }

    @Override
    public String getDtoClassName() {
        return "org.broadleafcommerce.core.order.domain.OrderItemImpl";
    }
}
