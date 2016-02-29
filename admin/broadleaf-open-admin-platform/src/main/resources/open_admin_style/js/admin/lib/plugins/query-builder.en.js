/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
/*!
 * jQuery QueryBuilder 2.2.0
 * Locale: English (en)
 * Author: Damien "Mistic" Sorel, http://www.strangeplanet.fr
 * Licensed under MIT (http://opensource.org/licenses/MIT)
 */

(function(root, factory) {
    if (typeof define === 'function' && define.amd) {
        define(['jquery', 'query-builder'], factory);
    }
    else {
        factory(root.jQuery);
    }
}(this, function($) {
"use strict";

var QueryBuilder = $.fn.queryBuilder;

QueryBuilder.regional['en'] = {
  "__locale": "English (en)",
  "__author": "Damien \"Mistic\" Sorel, http://www.strangeplanet.fr",
  "add_rule": "Add rule",
  "add_group": "Add group",
  "delete_rule": "Delete",
  "delete_group": "Delete",
  "conditions": {
    "AND": "ALL",
    "OR": "ANY"
  },
  "operators": {
    "EQUALS": "is equal to",
    "IEQUALS": "is equal to (ignore case)",
    "NOT_EQUAL": "is not equal",
    "INOT_EQUAL": "is not equal to (ignore case)",
    "IN_SET": "is in",
    "NOT_IN_SET": "is not in",
    "LESS_THAN": "is less than",
    "LESS_OR_EQUAL": "is less than or equal to",
    "GREATER_THAN": "is greater than",
    "GREATER_OR_EQUAL": "is greater than or equal to",
    "BETWEEN": "is between",
    "BETWEEN_INCLUSIVE": "is between (inclusive)",
    "ISTARTS_WITH": "starts with",
    "INOT_STARTS_WITH": "doesn't start with",
    "ICONTAINS": "contains",
    "CONTAINS": "contains",
    "INOT_CONTAINS": "doesn't contain",
    "NOT_CONTAINS": "doesn't contain",
    "IENDS_WITH": "ends with",
    "INOT_ENDS_WITH": "doesn't end with",
    "COUNT_GREATER_THAN": "is count greater than",
    "COUNT_GREATER_OR_EQUAL": "is count greater than or equal to",
    "COUNT_LESS_THAN": "is count less than",
    "COUNT_LESS_OR_EQUAL": "is count less than or equal to",
    "COUNT_EQUALS": "is count equal to",
    "COLLECTION_IN": "is in",
    "COLLECTION_NOT_IN": "is not in",
    "IS_NULL": "is blank"
  },
  "errors": {
    "no_filter": "No filter selected",
    "empty_group": "The group is empty",
    "radio_empty": "No value selected",
    "checkbox_empty": "No value selected",
    "select_empty": "No value selected",
    "string_empty": "Empty value",
    "string_exceed_min_length": "Must contain at least {0} characters",
    "string_exceed_max_length": "Must not contain more than {0} characters",
    "string_invalid_format": "Invalid format ({0})",
    "number_nan": "Not a number",
    "number_not_integer": "Not an integer",
    "number_not_double": "Not a real number",
    "number_exceed_min": "Must be greater than {0}",
    "number_exceed_max": "Must be lower than {0}",
    "number_wrong_step": "Must be a multiple of {0}",
    "datetime_empty": "Empty value",
    "datetime_invalid": "Invalid date format ({0})",
    "datetime_exceed_min": "Must be after {0}",
    "datetime_exceed_max": "Must be before {0}",
    "boolean_not_valid": "Not a boolean",
    "operator_not_multiple": "Operator {0} cannot accept multiple values"
  }
};

QueryBuilder.defaults({ lang_code: 'en' });
}));