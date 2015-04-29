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
var blcOperators_Boolean = [
    {label: "is equal to", name: "EQUALS", fieldType: "BOOLEAN"}
];

var blcOperators_Date = [      
	{label: "is equal to", name: "EQUALS", fieldType: "DATE"},
	{label: "is not equal to", name: "NOT_EQUAL", fieldType: "DATE"},
	{label: "is greater than", name: "GREATER_THAN", fieldType: "DATE"},
	{label: "is greater than or equal to", name: "GREATER_OR_EQUAL", fieldType: "DATE"},
	{label: "is less than", name: "LESS_THAN", fieldType: "DATE"},
	{label: "is less than or equal to", name: "LESS_OR_EQUAL", fieldType: "DATE"},
	{label: "is between", name: "BETWEEN", fieldType: "DATE_RANGE"},
	{label: "is between (inclusive)", name: "BETWEEN_INCLUSIVE", fieldType: "DATE_RANGE"}
];

var blcOperators_Numeric = [      
	{label: "is equal to", name: "EQUALS", fieldType: "TEXT"},
	{label: "is not equal to", name: "NOT_EQUAL", fieldType: "TEXT"},
	{label: "is greater than", name: "GREATER_THAN", fieldType: "TEXT"},
	{label: "is greater than or equal to", name: "GREATER_OR_EQUAL", fieldType: "TEXT"},
	{label: "is less than", name: "LESS_THAN", fieldType: "TEXT"},
	{label: "is less than or equal to", name: "LESS_OR_EQUAL", fieldType: "TEXT"},
	//{label: "is in the set", name: "IN_SET", fieldType: "TEXT"},
	//{label: "is not in the set", name: "NOT_IN_SET", fieldType: "TEXT"},
	{label: "is between", name: "BETWEEN", fieldType: "RANGE"},
	{label: "is between (inclusive)", name: "BETWEEN_INCLUSIVE", fieldType: "RANGE"}
];

var blcOperators_Text = [
	{label: "is blank", name: "IS_NULL", fieldType: "NONE"},
	{label: "is equal to", name: "EQUALS", fieldType: "TEXT"},
	{label: "is equal to (ignore case)", name: "IEQUALS", fieldType: "TEXT"},
	{label: "is not equal to", name: "NOT_EQUAL", fieldType: "TEXT"},
    {label: "is not equal to (ignore case)", name: "INOT_EQUAL", fieldType: "TEXT"},
	{label: "contains", name: "ICONTAINS", fieldType: "TEXT"},
	{label: "does not contain", name: "INOT_CONTAINS", fieldType: "TEXT"},
	{label: "starts with", name: "ISTARTS_WITH", fieldType: "TEXT"},
	{label: "does not start with", name: "INOT_STARTS_WITH", fieldType: "TEXT"},
	{label: "ends with", name: "IENDS_WITH", fieldType: "TEXT"},
	{label: "does not end with", name: "INOT_ENDS_WITH", fieldType: "TEXT"}
];

var blcOperators_Enumeration = [      
	{label: "is equal to", name: "EQUALS", fieldType: "SELECT"},
	{label: "is not equal to", name: "NOT_EQUAL", fieldType: "SELECT"}
];

var blcOperators_Text_List = [
    {label: "contains", name: "CONTAINS", fieldType: "TEXT"},
    {label: "does not contain", name: "NOT_CONTAINS", fieldType: "TEXT"},
    {label: "is count greater than", name: "COUNT_GREATER_THAN", fieldType: "TEXT"},
    {label: "is count greater than or equal to", name: "COUNT_GREATER_OR_EQUAL", fieldType: "TEXT"},
    {label: "is count less than", name: "COUNT_LESS_THAN", fieldType: "TEXT"},
    {label: "is count less than or equal to", name: "COUNT_LESS_OR_EQUAL", fieldType: "TEXT"},
    {label: "is count equal to", name: "COUNT_EQUALS", fieldType: "TEXT"}
];
