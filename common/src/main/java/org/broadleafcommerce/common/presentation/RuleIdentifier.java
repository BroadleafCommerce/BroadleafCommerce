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

package org.broadleafcommerce.common.presentation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class RuleIdentifier {

    public static final String CUSTOMER  = "CUSTOMER_FIELDS";
    public static final String FULFILLMENTGROUP  = "FULFILLMENT_GROUP_FIELDS";
    public static final String LOCALE  = "LOCALE_FIELDS";
    public static final String ORDER  = "ORDER_FIELDS";
    public static final String ORDERITEM  = "ORDER_ITEM_FIELDS";
    public static final String SKU = "SKU_FIELDS";
    public static final String TIME = "TIME_FIELDS";
    public static final String REQUEST = "REQUEST_FIELDS";
    public static final String PRICING_CONTEXT = "PRICING_CONTEXT_FIELDS";
    public static final String PRODUCT = "PRODUCT_FIELDS";
    public static final String CATEGORY = "CATEGORY_FIELDS";

    public static final String CUSTOMER_ENTITY_KEY = "customer";
    public static final String FULFILLMENTGROUP_ENTITY_KEY = "fulfillmentGroup";
    public static final String LOCALE_ENTITY_KEY = "locale";
    public static final String ORDER_ENTITY_KEY = "order";
    public static final String ORDERITEM_ENTITY_KEY = "orderItem";
    public static final String SKU_ENTITY_KEY = "sku";
    public static final String TIME_ENTITY_KEY = "time";
    public static final String REQUEST_ENTITY_KEY = "request";
    public static final String PRICING_CONTEXT_ENTITY_KEY = "pricingContext";
    public static final String PRODUCT_ENTITY_KEY = "product";
    public static final String CATEGORY_ENTITY_KEY = "category";

    public static final String CUSTOMER_FIELD_KEY = "CUSTOMER";
    public static final String LOCALE_FIELD_KEY = "LOCALE";
    public static final String ORDER_FIELD_KEY = "ORDER";
    public static final String FULFILLMENT_GROUP_FIELD_KEY = "FULFILLMENT_GROUP";
    public static final String TIME_FIELD_KEY = "TIME";
    public static final String REQUEST_FIELD_KEY = "REQUEST";
    public static final String PRICING_CONTEXT_FIELD_KEY = "PRICING_CONTEXT";
    public static final String PRODUCT_FIELD_KEY = "PRODUCT";
    public static final String CATEGORY_FIELD_KEY = "CATEGORY";

    public static Map<String, String> ENTITY_KEY_MAP = new HashMap<String, String>();
    static {
        ENTITY_KEY_MAP.put(CUSTOMER, CUSTOMER_ENTITY_KEY);
        ENTITY_KEY_MAP.put(FULFILLMENTGROUP, FULFILLMENTGROUP_ENTITY_KEY);
        ENTITY_KEY_MAP.put(LOCALE, LOCALE_ENTITY_KEY);
        ENTITY_KEY_MAP.put(ORDER, ORDER_ENTITY_KEY);
        ENTITY_KEY_MAP.put(ORDERITEM, ORDERITEM_ENTITY_KEY);
        ENTITY_KEY_MAP.put(SKU, SKU_ENTITY_KEY);
        ENTITY_KEY_MAP.put(TIME, TIME_ENTITY_KEY);
        ENTITY_KEY_MAP.put(REQUEST, REQUEST_ENTITY_KEY);
        ENTITY_KEY_MAP.put(PRODUCT, PRODUCT_ENTITY_KEY);
        ENTITY_KEY_MAP.put(CATEGORY, CATEGORY_ENTITY_KEY);
        ENTITY_KEY_MAP.put(PRICING_CONTEXT, PRICING_CONTEXT_ENTITY_KEY);
    }
}
