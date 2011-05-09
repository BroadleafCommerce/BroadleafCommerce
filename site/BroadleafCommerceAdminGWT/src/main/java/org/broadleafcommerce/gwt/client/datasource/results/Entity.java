package org.broadleafcommerce.gwt.client.datasource.results;

import java.io.Serializable;

public class Entity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String type;
	private Property[] properties;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Property[] getProperties() {
		return properties;
	}
	
	public void setProperties(Property[] properties) {
		this.properties = properties;
	}
	
}
