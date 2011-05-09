package org.broadleafcommerce.gwt.client.datasource.relations;

import java.io.Serializable;

public class ForeignKey implements Serializable, PersistencePerspectiveItem {

	private static final long serialVersionUID = 1L;
	
	private String manyToField;
	private String foreignKeyClass;
	private String currentValue;
	private String dataSourceName;
	private ForeignKeyRestrictionType restrictionType = ForeignKeyRestrictionType.ID_EQ;
	private String displayValueProperty = "name";
	
	public ForeignKey() {
		//do nothing
	}
	
	public ForeignKey(String manyToField, String foreignKeyClass) {
		this(manyToField, foreignKeyClass, null);
	}
	
	public ForeignKey(String manyToField, String foreignKeyClass, String dataSourceName) {
		this(manyToField, foreignKeyClass, dataSourceName, ForeignKeyRestrictionType.ID_EQ);
	}
	
	public ForeignKey(String manyToField, String foreignKeyClass, String dataSourceName, ForeignKeyRestrictionType restrictionType) {
		this(manyToField, foreignKeyClass, dataSourceName, restrictionType, "name");
	}
	
	public ForeignKey(String manyToField, String foreignKeyClass, String dataSourceName, ForeignKeyRestrictionType restrictionType, String displayValueProperty) {
		this.manyToField = manyToField;
		this.foreignKeyClass = foreignKeyClass;
		this.dataSourceName = dataSourceName;
		this.restrictionType = restrictionType;
		this.displayValueProperty = displayValueProperty;
	}
	
	public String getManyToField() {
		return manyToField;
	}
	
	public void setManyToField(String manyToField) {
		this.manyToField = manyToField;
	}
	
	public String getForeignKeyClass() {
		return foreignKeyClass;
	}
	
	public void setForeignKeyClass(String foreignKeyClass) {
		this.foreignKeyClass = foreignKeyClass;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public ForeignKeyRestrictionType getRestrictionType() {
		return restrictionType;
	}

	public void setRestrictionType(ForeignKeyRestrictionType restrictionType) {
		this.restrictionType = restrictionType;
	}

	public String getDisplayValueProperty() {
		return displayValueProperty;
	}

	public void setDisplayValueProperty(String displayValueProperty) {
		this.displayValueProperty = displayValueProperty;
	}

}
