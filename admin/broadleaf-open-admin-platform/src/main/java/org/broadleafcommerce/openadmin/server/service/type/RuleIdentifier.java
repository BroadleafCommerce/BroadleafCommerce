package org.broadleafcommerce.openadmin.server.service.type;

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

    public static final String CUSTOMER_ENTITY_KEY = "customer";
    public static final String FULFILLMENTGROUP_ENTITY_KEY = "fulfillmentGroup";
    public static final String LOCALE_ENTITY_KEY = "locale";
    public static final String ORDER_ENTITY_KEY = "order";
    public static final String ORDERITEM_ENTITY_KEY = "orderItem";
    public static final String SKU_ENTITY_KEY = "sku";

    public static final String CUSTOMER_FIELD_KEY = "CUSTOMER";
    public static final String LOCALE_FIELD_KEY = "LOCALE";

    public static Map<String, String> ENTITY_KEY_MAP = new HashMap<String, String>();
    static {
        ENTITY_KEY_MAP.put(CUSTOMER, CUSTOMER_ENTITY_KEY);
        ENTITY_KEY_MAP.put(FULFILLMENTGROUP, FULFILLMENTGROUP_ENTITY_KEY);
        ENTITY_KEY_MAP.put(LOCALE, LOCALE_ENTITY_KEY);
        ENTITY_KEY_MAP.put(ORDER, ORDER_ENTITY_KEY);
        ENTITY_KEY_MAP.put(ORDERITEM, ORDERITEM_ENTITY_KEY);
        ENTITY_KEY_MAP.put(SKU, SKU_ENTITY_KEY);
    }
}
