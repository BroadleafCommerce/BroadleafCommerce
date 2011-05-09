package org.broadleafcommerce.gwt.client.datasource.results;

import java.io.Serializable;

public class ClassMetadata implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String ceilingType;
	private PolymorphicEntity[] polymorphicEntities; 
	private Property[] properties;
	
	public String getCeilingType() {
		return ceilingType;
	}
	
	public void setCeilingType(String type) {
		this.ceilingType = type;
	}
	
	public PolymorphicEntity[] getPolymorphicEntities() {
		return polymorphicEntities;
	}
	
	public void setPolymorphicEntities(PolymorphicEntity[] polymorphicEntities) {
		this.polymorphicEntities = polymorphicEntities;
	}
	
	public Property[] getProperties() {
		return properties;
	}
	
	public void setProperties(Property[] property) {
		this.properties = property;
	}
	
}
