var blcOperators_Boolean = [      
	{label: "is present", name: "NOT_NULL", fieldType: "NONE"},
	{label: "is blank", name: "IS_NULL", fieldType: "NONE"}
];

var blcOperators_Date = [      
	{label: "is present", name: "NOT_NULL", fieldType: "NONE"},
	{label: "is blank", name: "IS_NULL", fieldType: "NONE"},
	{label: "is equal to", name: "EQUALS", fieldType: "TEXT"},
	{label: "is not equal to", name: "NOT_EQUAL", fieldType: "TEXT"},
	{label: "is greater than", name: "GREATER_THAN", fieldType: "TEXT"},
	{label: "is greater than or equal to", name: "GREATER_OR_EQUAL", fieldType: "TEXT"},
	{label: "is less than", name: "LESS_THAN", fieldType: "TEXT"},
	{label: "is less than or equal to", name: "LESS_OR_EQUAL", fieldType: "TEXT"},
	{label: "is between", name: "BETWEEN", fieldType: "RANGE"},
	{label: "is between (inclusive)", name: "BETWEEN_INCLUSIVE", fieldType: "RANGE"}
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
	{label: "is in the set", name: "IN_SET", fieldType: "TEXT"},
	{label: "is not in the set", name: "NOT_IN_SET", fieldType: "TEXT"},	
	{label: "is between", name: "BETWEEN", fieldType: "RANGE"},
	{label: "is between (inclusive)", name: "BETWEEN_INCLUSIVE", fieldType: "RANGE"}
];

var blcOperators_Text = [      
	{label: "is present", name: "NOT_NULL", fieldType: "NONE"},
	{label: "is blank", name: "IS_NULL", fieldType: "NONE"},
	{label: "is equal to", name: "EQUALS", fieldType: "TEXT"},
	{label: "is equal to (ignore case)", name: "IEQUALS", fieldType: "TEXT"},
	{label: "is not equal to", name: "NOT_EQUAL", fieldType: "TEXT"},
	{label: "contains", name: "CONTAINS", fieldType: "TEXT"},
	{label: "does not contain", name: "NOT_CONTAINS", fieldType: "TEXT"},
	{label: "starts with", name: "STARTS_WITH", fieldType: "TEXT"},
	{label: "does not start with", name: "NOT_STARTS_WITH", fieldType: "TEXT"},		
	{label: "ends with", name: "ENDS_WITH", fieldType: "TEXT"},
	{label: "does not end with", name: "NOT_ENDS_WITH", fieldType: "TEXT"}	
];

var blcOperators_Enumeration = [      
	{label: "is present", name: "NOT_NULL", fieldType: "NONE"},
	{label: "is blank", name: "IS_NULL", fieldType: "NONE"},
	{label: "is equal to", name: "EQUALS", fieldType: "SELECT"},
	{label: "is not equal to", name: "NOT_EQUAL", fieldType: "SELECT"}
];



