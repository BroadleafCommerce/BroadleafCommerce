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
package org.broadleafcommerce.common.presentation.client;

/**
 * 
 * @author jfischer
 *
 */
public enum SupportedFieldType {
    UNKNOWN,
    ID,
    BOOLEAN,
    BOOLEAN_LINK,
    DATE,
    INTEGER,
    DECIMAL,
    STRING,
    COLLECTION,
    PASSWORD,
    PASSWORD_CONFIRM,
    EMAIL,
    FOREIGN_KEY,
    ADDITIONAL_FOREIGN_KEY,
    MONEY,
    BROADLEAF_ENUMERATION,
    EXPLICIT_ENUMERATION,
    EMPTY_ENUMERATION,
    DATA_DRIVEN_ENUMERATION,
    HTML,
    HTML_BASIC,
    UPLOAD,
    HIDDEN,
    ASSET_URL,
    ASSET_LOOKUP,
    MEDIA,
    RULE_SIMPLE,
    RULE_SIMPLE_TIME,
    RULE_WITH_QUANTITY,
    STRING_LIST,
    IMAGE,
    COLOR,
    CODE,
    GENERATED_URL,
    GENERATED_FIELD_VALUE,
    DESCRIPTION,
    WITHIN_DAYS
}
