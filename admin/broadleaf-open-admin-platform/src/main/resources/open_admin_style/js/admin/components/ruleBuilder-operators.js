var blcOperators_Boolean = [
    {label: "is equal to", name: "EQUALS", fieldType: "BOOLEAN"}
];

var blcOperators_Date = [      
	{label: "is present", name: "NOT_NULL", fieldType: "NONE"},
	{label: "is blank", name: "IS_NULL", fieldType: "NONE"},
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
	{label: "is present", name: "NOT_NULL", fieldType: "NONE"},
	{label: "is blank", name: "IS_NULL", fieldType: "NONE"},
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
	{label: "is present", name: "NOT_NULL", fieldType: "NONE"},
	{label: "is blank", name: "IS_NULL", fieldType: "NONE"},
	{label: "is equal to", name: "EQUALS", fieldType: "TEXT"},
	{label: "is equal to (ignore case)", name: "IEQUALS", fieldType: "TEXT"},
	{label: "is not equal to", name: "NOT_EQUAL", fieldType: "TEXT"},
    {label: "is not equal to (ignore case)", name: "INOT_EQUAL", fieldType: "TEXT"},
	{label: "contains", name: "ICONTAINS", fieldType: "TEXT"},
	{label: "not contains", name: "INOT_CONTAINS", fieldType: "TEXT"},
	{label: "starts with", name: "ISTARTS_WITH", fieldType: "TEXT"},
	{label: "does not start with", name: "INOT_STARTS_WITH", fieldType: "TEXT"},
	{label: "ends with", name: "IENDS_WITH", fieldType: "TEXT"},
	{label: "does not end with", name: "INOT_ENDS_WITH", fieldType: "TEXT"}
];

var blcOperators_Enumeration = [      
	{label: "is present", name: "NOT_NULL", fieldType: "NONE"},
	{label: "is blank", name: "IS_NULL", fieldType: "NONE"},
	{label: "is equal to", name: "EQUALS", fieldType: "SELECT"},
	{label: "is not equal to", name: "NOT_EQUAL", fieldType: "SELECT"}
];

var blcOperators_Text_List = [
    {label: "is present", name: "NOT_NULL", fieldType: "NONE"},
    {label: "is blank", name: "IS_NULL", fieldType: "NONE"},
    {label: "contains", name: "CONTAINS", fieldType: "TEXT"},
    {label: "is count greater than", name: "COUNT_GREATER_THAN", fieldType: "TEXT"},
    {label: "is count greater than or equal to", name: "COUNT_GREATER_OR_EQUAL", fieldType: "TEXT"},
    {label: "is count less than", name: "COUNT_LESS_THAN", fieldType: "TEXT"},
    {label: "is count less than or equal to", name: "COUNT_LESS_OR_EQUAL", fieldType: "TEXT"},
    {label: "is count equal to", name: "COUNT_EQUALS", fieldType: "TEXT"}
];
