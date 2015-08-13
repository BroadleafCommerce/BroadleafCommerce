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

var blcOperators = [
    {type: "IS_NULL", nb_inputs: 0, multiple: false, apply_to: ['string']},
    {type: "EQUALS", nb_inputs: 1, multiple: false, apply_to: ['boolean', 'string', 'number', 'datetime']},
    {type: "IEQUALS", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "NOT_EQUAL", nb_inputs: 1, multiple: false, apply_to: ['string', 'number', 'datetime']},
    {type: "INOT_EQUAL", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "CONTAINS", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "ICONTAINS", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "NOT_CONTAINS", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "INOT_CONTAINS", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "ISTARTS_WITH", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "INOT_STARTS_WITH", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "IENDS_WITH", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "INOT_ENDS_WITH", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "COLLECTION_IN", nb_inputs: 1, multiple: true, apply_to: ['string']},
    {type: "COLLECTION_NOT_IN", nb_inputs: 1, multiple: true, apply_to: ['string']},
    {type: "COUNT_GREATER_THAN", nb_inputs: 1, multiple: false, apply_to: ['number']},
    {type: "COUNT_GREATER_OR_EQUAL", nb_inputs: 1, multiple: false, apply_to: ['number']},
    {type: "COUNT_LESS_THAN", nb_inputs: 1, multiple: false, apply_to: ['number']},
    {type: "COUNT_LESS_OR_EQUAL", nb_inputs: 1, multiple: false, apply_to: ['number']},
    {type: "COUNT_EQUALS", nb_inputs: 1, multiple: false, apply_to: ['number']},
    {type: "GREATER_THAN", nb_inputs: 1, multiple: false, apply_to: ['number','datetime']},
    {type: "GREATER_OR_EQUAL", nb_inputs: 1, multiple: false, apply_to: ['number','datetime']},
    {type: "LESS_THAN", nb_inputs: 1, multiple: false, apply_to: ['number','datetime']},
    {type: "LESS_OR_EQUAL", nb_inputs: 1, multiple: false, apply_to: ['number','datetime']},
    {type: "BETWEEN", nb_inputs: 2, multiple: false, apply_to: ['number','datetime']},
    {type: "BETWEEN_INCLUSIVE", nb_inputs: 2, multiple: false, apply_to: ['number','datetime']}
];

var blcOperators_Boolean = [
    "EQUALS"
];

var blcOperators_Selectize = [
    "COLLECTION_IN",
    "COLLECTION_NOT_IN"
];

var blcOperators_Date = [
    "EQUALS",
    "NOT_EQUAL",
    "GREATER_THAN",
    "GREATER_OR_EQUAL",
    "LESS_THAN",
    "LESS_OR_EQUAL",
    "BETWEEN",
    "BETWEEN_INCLUSIVE"
];

var blcOperators_Numeric = [
    "EQUALS",
    "NOT_EQUAL",
    "GREATER_THAN",
    "GREATER_OR_EQUAL",
    "LESS_THAN",
    "LESS_OR_EQUAL",
    "BETWEEN",
    "BETWEEN_INCLUSIVE"
];

var blcOperators_Text = [
    "IS_NULL",
    "EQUALS",
    "IEQUALS",
    "NOT_EQUAL",
    "INOT_EQUAL",
    "ICONTAINS",
    "INOT_CONTAINS",
    "ISTARTS_WITH",
    "INOT_STARTS_WITH",
    "IENDS_WITH",
    "INOT_ENDS_WITH"];

var blcOperators_Enumeration = [
    "EQUALS",
    "NOT_EQUAL"
];

var blcOperators_Text_List = [
    "CONTAINS",
    "NOT_CONTAINS",
    "COUNT_GREATER_THAN",
    "COUNT_GREATER_OR_EQUAL",
    "COUNT_LESS_THAN",
    "COUNT_LESS_OR_EQUAL",
    "COUNT_EQUALS"
];
