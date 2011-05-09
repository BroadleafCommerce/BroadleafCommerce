package org.broadleafcommerce.gwt.client.datasource.relations;

import java.io.Serializable;

public class ForeignKey implements Serializable, PersistencePerspectiveItem {

	private static final long serialVersionUID = 1L;
	
	private String manyToField;
	private String foreignKeyClass;
	private String currentValue;
	private String dataSourceName;
	
	public ForeignKey() {
		//do nothing
	}
	
	public ForeignKey(String manyToField, String foreignKeyClass, String dataSourceName) {
		this.manyToField = manyToField;
		this.foreignKeyClass = foreignKeyClass;
		this.dataSourceName = dataSourceName;
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

}
