package org.broadleafcommerce.gwt.client.datasource.relations;

public class SimpleValueMapStructure extends MapStructure {

	private static final long serialVersionUID = 1L;
	
	private String valuePropertyName;
	private String valuePropertyFriendlyName;
	
	public SimpleValueMapStructure() {
		super();
	}
	
	/**
	 * @param keyClassName
	 * @param keyPropertyName
	 * @param keyPropertyFriendlyName
	 * @param valueClassName
	 * @param mapProperty
	 * @param deleteValueEntity
	 */
	public SimpleValueMapStructure(String keyClassName, String keyPropertyName, String keyPropertyFriendlyName, String valueClassName, String valuePropertyName, String valuePropertyFriendlyName, String mapProperty) {
		super(keyClassName, keyPropertyName, keyPropertyFriendlyName, valueClassName, mapProperty, false);
		this.valuePropertyFriendlyName = valuePropertyFriendlyName;
		this.valuePropertyName = valuePropertyName;
	}

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
