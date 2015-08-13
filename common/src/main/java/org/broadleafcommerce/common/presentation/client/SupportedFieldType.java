/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
    RULE_WITH_QUANTITY,
    RULE_WITHOUT_QUESTION,
    STRING_LIST,
    IMAGE,
    COLOR,
    CODE,
    GENERATED_URL
}
