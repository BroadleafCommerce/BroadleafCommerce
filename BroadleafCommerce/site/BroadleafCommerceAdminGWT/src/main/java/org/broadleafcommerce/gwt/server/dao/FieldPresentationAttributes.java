package org.broadleafcommerce.gwt.server.dao;

import java.io.Serializable;

import org.broadleafcommerce.presentation.SupportedFieldType;

public class FieldPresentationAttributes implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String friendlyName;
	private int order;
	private boolean hidden;
	private String group;
	private SupportedFieldType fieldType;
	private boolean largeEntry;
	private boolean prominent;
	
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
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public SupportedFieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(SupportedFieldType fieldType) {
		this.fieldType = fieldType;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isLargeEntry() {
		return largeEntry;
	}

	public void setLargeEntry(boolean largeEntry) {
		this.largeEntry = largeEntry;
	}

	public boolean isProminent() {
		return prominent;
	}

	public void setProminent(boolean prominent) {
		this.prominent = prominent;
	}

}
