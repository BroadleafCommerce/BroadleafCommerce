package org.broadleafcommerce.gwt.client.datasource.relations;

import java.io.Serializable;

public class MapStructure implements Serializable, PersistencePerspectiveItem {

	private static final long serialVersionUID = 1L;
	
	private String keyClassName;
	private String keyPropertyName;
	private String keyPropertyFriendlyName;
	private String valueClassName;
	private String mapProperty;
	private Boolean deleteValueEntity = Boolean.FALSE;
	
	public MapStructure() {
		//do nothing - support serialization requirements
	}
	
	public MapStructure(String keyClassName, String keyPropertyName, String keyPropertyFriendlyName, String valueClassName, String mapProperty, Boolean deleteValueEntity) {
		this.keyClassName = keyClassName;
		this.valueClassName = valueClassName;
		this.mapProperty = mapProperty;
		this.keyPropertyName = keyPropertyName;
		this.keyPropertyFriendlyName = keyPropertyFriendlyName;
		this.deleteValueEntity = deleteValueEntity;
	}
	
	public String getKeyClassName() {
		return keyClassName;
	}
	
	public void setKeyClassName(String keyClassName) {
		this.keyClassName = keyClassName;
	}
	
	public String getValueClassName() {
		return valueClassName;
	}
	
	public void setValueClassName(String valueClassName) {
		this.valueClassName = valueClassName;
	}
	
	public String getMapProperty() {
		return mapProperty;
	}
	
	public void setMapProperty(String mapProperty) {
		this.mapProperty = mapProperty;
	}

	public String getKeyPropertyName() {
		return keyPropertyName;
	}

	public void setKeyPropertyName(String keyPropertyName) {
		this.keyPropertyName = keyPropertyName;
	}

	public String getKeyPropertyFriendlyName() {
		return keyPropertyFriendlyName;
	}

	public void setKeyPropertyFriendlyName(String keyPropertyFriendlyName) {
		this.keyPropertyFriendlyName = keyPropertyFriendlyName;
	}

	public Boolean getDeleteValueEntity() {
		return deleteValueEntity;
	}

	public void setDeleteValueEntity(Boolean deleteValueEntity) {
		this.deleteValueEntity = deleteValueEntity;
	}

}
