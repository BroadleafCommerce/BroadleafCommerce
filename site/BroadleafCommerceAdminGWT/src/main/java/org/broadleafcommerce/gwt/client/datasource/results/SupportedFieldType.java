package org.broadleafcommerce.gwt.client.datasource.results;

public enum SupportedFieldType {
	ID,
	BOOLEAN,
	DATE,
	INTEGER,
	DECIMAL,
	STRING,
	EMAIL,
	HIERARCHY_KEY,
	FOREIGN_KEY,
	MONEY;
	
	//TODO add a new type that accounts for our extendible enumerations
}
