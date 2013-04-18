/*
 * Broadleaf Commerce Confidential
 * _______________________________
 *
 * [2009] - [2013] Broadleaf Commerce, LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 */

package com.broadleafcommerce.customfield.service;

import com.broadleafcommerce.customfield.service.type.CustomFieldTargetType;
import com.broadleafcommerce.customfield.service.type.CustomFieldType;
import org.broadleafcommerce.openadmin.server.service.type.RuleIdentifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Useful constants and relationship maps for custom fields.
 *
 * @author Jeff Fischer
 */
public class CustomFieldInfo {

    private static final String PRODUCT_ATTRIBUTE_FIELD = "productAttributes";
    private static final String SKU_ATTRIBUTE_FIELD = "skuAttributes";
    private static final String ORDER_ITEM_ATTRIBUTE_FIELD = "orderItemAttributeMap";
    private static final String CUSTOMER_ATTRIBUTE_FIELD = "customerAttributes";

    public static final Map<String, String> CUSTOM_FIELD_FIELD_NAMES = new HashMap<String, String>();
    static {
        CUSTOM_FIELD_FIELD_NAMES.put(CustomFieldTargetType.CUSTOMER.getType(), CUSTOMER_ATTRIBUTE_FIELD);
        CUSTOM_FIELD_FIELD_NAMES.put(CustomFieldTargetType.ORDERITEM.getType(), ORDER_ITEM_ATTRIBUTE_FIELD);
        CUSTOM_FIELD_FIELD_NAMES.put(CustomFieldTargetType.PRODUCT.getType(), PRODUCT_ATTRIBUTE_FIELD);
        CUSTOM_FIELD_FIELD_NAMES.put(CustomFieldTargetType.SKU.getType(), SKU_ATTRIBUTE_FIELD);
    }

    private static final String BLCOPERATORS_BOOLEAN = "blcOperators_Boolean";
    private static final String BLCOPERATORS_DATE = "blcOperators_Date";
    private static final String BLCOPERATOS_NUMERIC = "blcOperators_Numeric";
    private static final String BLCOPERATORS_TEXT = "blcOperators_Text";
    private static final String BLCOPERATORS_ENUMERATION = "blcOperators_Enumeration";
    private static final String BLCOPERATORS_TEXT_LIST = "blcOperators_Text_List";

    public static final Map<CustomFieldType, String> CUSTOM_FIELD_RULE_OPERATORS = new HashMap<CustomFieldType, String>();
    static {
        CUSTOM_FIELD_RULE_OPERATORS.put(CustomFieldType.BOOLEAN, BLCOPERATORS_BOOLEAN);
        CUSTOM_FIELD_RULE_OPERATORS.put(CustomFieldType.DATE, BLCOPERATORS_DATE);
        CUSTOM_FIELD_RULE_OPERATORS.put(CustomFieldType.DECIMAL, BLCOPERATOS_NUMERIC);
        CUSTOM_FIELD_RULE_OPERATORS.put(CustomFieldType.INTEGER, BLCOPERATOS_NUMERIC);
        CUSTOM_FIELD_RULE_OPERATORS.put(CustomFieldType.MONEY, BLCOPERATOS_NUMERIC);
        CUSTOM_FIELD_RULE_OPERATORS.put(CustomFieldType.STRING, BLCOPERATORS_TEXT);
        CUSTOM_FIELD_RULE_OPERATORS.put(CustomFieldType.STRING_LIST, BLCOPERATORS_TEXT_LIST);
    }

    public static final Map<String, CustomFieldTargetType[]> RULE_BUILDER_FIELD_SERVICES = new HashMap<String, CustomFieldTargetType[]>();
    static {
        RULE_BUILDER_FIELD_SERVICES.put(RuleIdentifier.CUSTOMER, new CustomFieldTargetType[]{CustomFieldTargetType.CUSTOMER});
        RULE_BUILDER_FIELD_SERVICES.put(RuleIdentifier.ORDERITEM, new CustomFieldTargetType[]{CustomFieldTargetType.ORDERITEM, CustomFieldTargetType.PRODUCT, CustomFieldTargetType.SKU});
        RULE_BUILDER_FIELD_SERVICES.put(RuleIdentifier.PRODUCT, new CustomFieldTargetType[]{CustomFieldTargetType.PRODUCT});
        RULE_BUILDER_FIELD_SERVICES.put(RuleIdentifier.SKU, new CustomFieldTargetType[]{CustomFieldTargetType.SKU});
    }

    public static final Map<String, Map<CustomFieldTargetType, String>> RULE_BUILDER_TYPE_PREFIXES = new HashMap<String, Map<CustomFieldTargetType, String>>();
    static {
        Map<CustomFieldTargetType, String> customerMap = new HashMap<CustomFieldTargetType, String>();
        customerMap.put(CustomFieldTargetType.CUSTOMER, "");
        RULE_BUILDER_TYPE_PREFIXES.put(RuleIdentifier.CUSTOMER, customerMap);

        Map<CustomFieldTargetType, String> orderItemMap = new HashMap<CustomFieldTargetType, String>();
        orderItemMap.put(CustomFieldTargetType.ORDERITEM, "");
        orderItemMap.put(CustomFieldTargetType.PRODUCT, "product.");
        orderItemMap.put(CustomFieldTargetType.SKU, "sku.");
        RULE_BUILDER_TYPE_PREFIXES.put(RuleIdentifier.ORDERITEM, orderItemMap);

        Map<CustomFieldTargetType, String> productMap = new HashMap<CustomFieldTargetType, String>();
        productMap.put(CustomFieldTargetType.PRODUCT, "");
        RULE_BUILDER_TYPE_PREFIXES.put(RuleIdentifier.PRODUCT, productMap);

        Map<CustomFieldTargetType, String> skuMap = new HashMap<CustomFieldTargetType, String>();
        skuMap.put(CustomFieldTargetType.SKU, "");
        RULE_BUILDER_TYPE_PREFIXES.put(RuleIdentifier.SKU, skuMap);
    }
}
