/*
 * #%L
 * BroadleafCommerce Common Libraries
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
    public static final String TRANSACTION = "TRANSACTION_FIELDS";

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
    public static final String TRANSACTION_KEY = "transaction";

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
        ENTITY_KEY_MAP.put(TRANSACTION, TRANSACTION_KEY);
    }
}
