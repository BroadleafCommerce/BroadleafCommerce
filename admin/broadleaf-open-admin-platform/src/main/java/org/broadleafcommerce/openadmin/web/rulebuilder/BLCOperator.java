/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.rulebuilder;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public enum BLCOperator {

    EQUALS, NOT_EQUAL, IEQUALS, INOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_OR_EQUAL, LESS_OR_EQUAL, CONTAINS,
    STARTS_WITH, ENDS_WITH, ICONTAINS, ISTARTS_WITH, IENDS_WITH, NOT_CONTAINS, NOT_STARTS_WITH, NOT_ENDS_WITH,
    INOT_CONTAINS, INOT_STARTS_WITH, INOT_ENDS_WITH, REGEXP, IREGEXP, IS_NULL, NOT_NULL, IN_SET, NOT_IN_SET,
    EQUALS_FIELD, NOT_EQUAL_FIELD, GREATER_THAN_FIELD, LESS_THAN_FIELD, GREATER_OR_EQUAL_FIELD, LESS_OR_EQUAL_FIELD,
    CONTAINS_FIELD, STARTS_WITH_FIELD, ENDS_WITH_FIELD, AND, NOT, OR, BETWEEN, BETWEEN_INCLUSIVE, COUNT_GREATER_THAN,
    COUNT_GREATER_OR_EQUAL, COUNT_LESS_THAN, COUNT_LESS_OR_EQUAL, COUNT_EQUALS, COLLECTION_IN, COLLECTION_NOT_IN

}
