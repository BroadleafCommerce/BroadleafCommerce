package org.broadleafcommerce.gwt.client.datasource.results;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.presentation.SupportedFieldType;

public class FieldPresentationAttributes implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String friendlyName;
	private String securityLevel;
	private Integer order;
	private Boolean hidden;
	private String group;
	private Integer groupOrder;
	private SupportedFieldType explicitFieldType;
	private Boolean largeEntry;
	private Boolean prominent;
	private String columnWidth;
	private String broadleafEnumeration;
	private Boolean readOnly;
	private Map<String, Map<String, String>> validationConfigurations = new HashMap<String, Map<String, String>>();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFriendlyName() {
		return friendlyName;
	}
	
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	
	public String getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(String securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public Boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public SupportedFieldType getExplicitFieldType() {
		return explicitFieldType;
	}

	public void setExplicitFieldType(SupportedFieldType fieldType) {
		this.explicitFieldType = fieldType;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Boolean isLargeEntry() {
		return largeEntry;
	}

	public void setLargeEntry(Boolean largeEntry) {
		this.largeEntry = largeEntry;
	}

	public Boolean isProminent() {
		return prominent;
	}

	public void setProminent(Boolean prominent) {
		this.prominent = prominent;
	}

	public String getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(String columnWidth) {
		this.columnWidth = columnWidth;
	}

	public String getBroadleafEnumeration() {
		return broadleafEnumeration;
	}

	public void setBroadleafEnumeration(String broadleafEnumeration) {
		this.broadleafEnumeration = broadleafEnumeration;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Integer getGroupOrder() {
		return groupOrder;
	}

	public void setGroupOrder(Integer groupOrder) {
		this.groupOrder = groupOrder;
	}

	public Map<String, Map<String, String>> getValidationConfigurations() {
		return validationConfigurations;
	}

	public void setValidationConfigurations(
			Map<String, Map<String, String>> validationConfigurations) {
		this.validationConfigurations = validationConfigurations;
	}

}
