/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

var blcOperators = [
    {type: "IS_NULL", nb_inputs: 0, multiple: false, apply_to: ['string']},
    {type: "EQUALS", nb_inputs: 1, multiple: false, apply_to: ['boolean', 'string', 'number', 'datetime']},
    {type: "IEQUALS", nb_inputs: 1, multiple: false, apply_to: ['string']},
    {type: "NOT_EQUAL", nb_inputs: 1, multiple: false, apply_to: ['boolean', 'string', 'number', 'datetime']},
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
    "EQUALS",
    "NOT_EQUAL"
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

var blcOperators_Selectize_Enumeration = [
    "COLLECTION_IN",
    "COLLECTION_NOT_IN"
];

var blcOperators_Text_List = [
    "COLLECTION_IN",
    "COLLECTION_NOT_IN",
    "CONTAINS",
    "NOT_CONTAINS",
    "COUNT_GREATER_THAN",
    "COUNT_GREATER_OR_EQUAL",
    "COUNT_LESS_THAN",
    "COUNT_LESS_OR_EQUAL",
    "COUNT_EQUALS"
];

var blcFilterOperators_Text = [
    "CONTAINS"
];

var blcFilterOperators_Numeric = [
    "EQUALS",
    "BETWEEN"
];

var blcFilterOperators_Date = [
    "EQUALS",
    "BETWEEN"
];

var blcFilterOperators_Boolean = [
    "EQUALS"
];

var blcFilterOperators_Enumeration = [
    "EQUALS"
];

var blcFilterOperators_Selectize = [
    "COLLECTION_IN"
];
