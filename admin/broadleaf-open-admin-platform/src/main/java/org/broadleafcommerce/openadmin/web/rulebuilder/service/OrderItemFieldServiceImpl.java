/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.rulebuilder.service;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;
import org.springframework.stereotype.Service;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blOrderItemFieldService")
public class OrderItemFieldServiceImpl extends AbstractRuleBuilderFieldService {

    //Fields are block initialized for now.
    //next steps: extensibility mechanism, support i18N
    {
        fields.add(new FieldData.Builder()
                .label("Order Item - Item Name")
                .name("name")
                .operators("blcOperators_Text")
                .options("[]").build());
        fields.add(new FieldData.Builder()
                .label("Order Item - Item Price")
                .name("basePrice")
                .operators("blcOperators_Numeric")
                .options("[]").build());
        fields.add(new FieldData.Builder()
                .label("Order Item - Item Quantity")
                .name("quantity")
                .operators("blcOperators_Numeric")
                .options("[]").build());
        fields.add(new FieldData.Builder()
                .label("Order Item - Item Retail Price")
                .name("retailPrice")
                .operators("blcOperators_Numeric")
                .options("[]").build());
        fields.add(new FieldData.Builder()
                .label("Order Item - Item Sale Price")
                .name("salePrice")
                .operators("blcOperators_Numeric")
                .options("[]").build());
        fields.add(new FieldData.Builder()
                .label("Category - ID")
                .name("category.id")
                .operators("blcOperators_Text")
                .options("[]").build());
        fields.add(new FieldData.Builder()
                .label("Category - Name")
                .name("category.name")
                .operators("blcOperators_Text")
                .options("[]").build());
        fields.add(new FieldData.Builder()
                .label("Category - Fulfillment Type")
                .name("category.fulfillmentType")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_FulfillmentType").build());
        fields.add(new FieldData.Builder()
                .label("Category - Inventory Type")
                .name("category.inventoryType")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_InventoryType").build());
        fields.add(new FieldData.Builder()
                .label("Category - URL")
                .name("category.url")
                .operators("blcOperators_Text")
                .options("[]").build());
        fields.add(new FieldData.Builder()
                .label("Product - URL")
                .name("product.url")
                .operators("blcOperators_Text")
                .options("[]").build());
        fields.add(new FieldData.Builder()
                .label("Product - Is Featured Product")
                .name("product.isFeaturedProduct")
                .operators("blcOperators_Boolean")
                .options("[]").build());
        fields.add(new FieldData.Builder()
                .label("Sku - Fulfillment Type")
                .name("sku.fulfillmentType")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_FulfillmentType").build());
        fields.add(new FieldData.Builder()
                .label("Sku - Inventory Type")
                .name("sku.inventoryType")
                .operators("blcOperators_Enumeration")
                .options("blcOptions_InventoryType").build());
    }

    @Override
    public String getName() {
        return "ORDER_ITEM_FIELDS";
    }

}
