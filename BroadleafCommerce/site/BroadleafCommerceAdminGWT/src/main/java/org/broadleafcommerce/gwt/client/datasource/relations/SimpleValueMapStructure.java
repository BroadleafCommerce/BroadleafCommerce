package org.broadleafcommerce.gwt.client.datasource.relations;

public class SimpleValueMapStructure extends MapStructure {

	private static final long serialVersionUID = 1L;
	
	private String valuePropertyName;
	private String valuePropertyFriendlyName;
	
	public String getValuePropertyName() {
		return valuePropertyName;
	}
	
	public void setValuePropertyName(String valuePropertyName) {
		this.valuePropertyName = valuePropertyName;
	}
	
	public String getValuePropertyFriendlyName() {
		return valuePropertyFriendlyName;
	}
	
	public void setValuePropertyFriendlyName(String valuePropertyFriendlyName) {
		this.valuePropertyFriendlyName = valuePropertyFriendlyName;
	}
	
}
