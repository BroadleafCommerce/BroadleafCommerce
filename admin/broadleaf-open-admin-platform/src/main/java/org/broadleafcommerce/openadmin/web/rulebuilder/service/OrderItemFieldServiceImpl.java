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

import org.springframework.stereotype.Service;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blOrderItemFieldService")
public class OrderItemFieldServiceImpl extends AbstractRuleBuilderFieldService {

    //Fields are block initialized for now.
    //String array of the form: ["label"|"name"|"operators"|"options"]
    //next steps: extensibility mechanism, support i18N
    {
        fields.add("Order Item - Item Name|name|blcOperators_Text|[]");
        fields.add("Order Item - Item Price|basePrice|blcOperators_Numeric|[]");
        fields.add("Order Item - Item Quantity|quantity|blcOperators_Numeric|[]");
        fields.add("Order Item - Item Retail Price|retailPrice|blcOperators_Numeric|[]");
        fields.add("Order Item - Item Sale Price|salePrice|blcOperators_Numeric|[]");
        fields.add("Category - ID|category.id|blcOperators_Text|[]");
        fields.add("Category - Name|category.name|blcOperators_Text|[]");
        fields.add("Category - Fulfillment Type|category.fulfillmentType|blcOperators_Enumeration|blcOptions_FulfillmentType");
        fields.add("Category - Inventory Type|category.inventoryType|blcOperators_Enumeration|blcOptions_InventoryType");
        fields.add("Category - URL|category.url|blcOperators_Text|[]");
        fields.add("Product - URL|product.url|blcOperators_Text|[]");
        fields.add("Product - Is Featured Product|product.isFeaturedProduct|blcOperators_Boolean|[]");
        fields.add("Sku - Fulfillment Type|sku.fulfillmentType|blcOperators_Enumeration|blcOptions_FulfillmentType");
        fields.add("Sku - Inventory Type|sku.inventoryType|blcOperators_Enumeration|blcOptions_InventoryType");
    }

    @Override
    public String getName() {
        return "ORDER_ITEM_FIELDS";
    }
}
