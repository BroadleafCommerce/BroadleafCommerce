package com.broadleafcommerce.customfield.service;

import com.broadleafcommerce.customfield.service.type.CustomFieldTargetType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class CustomFieldInfo {

    public static final String PRODUCT_ATTRIBUTE_FIELD = "productAttributes";
    public static final String SKU_ATTRIBUTE_FIELD = "skuAttributes";
    public static final String ORDER_ITEM_ATTRIBUTE_FIELD = "orderItemAttributeMap";
    public static final String CUSTOMER_ATTRIBUTE_FIELD = "customerAttributes";

    public static final Map<String, String> CUSTOM_FIELD_FIELD_NAMES = new HashMap<String, String>();
    static {
        CUSTOM_FIELD_FIELD_NAMES.put(CustomFieldTargetType.CUSTOMER.getType(), CUSTOMER_ATTRIBUTE_FIELD);
        CUSTOM_FIELD_FIELD_NAMES.put(CustomFieldTargetType.ORDERITEM.getType(), ORDER_ITEM_ATTRIBUTE_FIELD);
        CUSTOM_FIELD_FIELD_NAMES.put(CustomFieldTargetType.PRODUCT.getType(), PRODUCT_ATTRIBUTE_FIELD);
        CUSTOM_FIELD_FIELD_NAMES.put(CustomFieldTargetType.SKU.getType(), SKU_ATTRIBUTE_FIELD);
    }
}
