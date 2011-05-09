package org.broadleafcommerce.gwt.client.datasource.results;

import java.io.Serializable;


public class Property implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String value;
	private String displayValue;
	private FieldMetadata metadata = new FieldMetadata();

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public FieldMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(FieldMetadata metadata) {
		this.metadata = metadata;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metadata == null || metadata.getMergedPropertyType() == null) ? 0 : metadata.getMergedPropertyType().hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Property other = (Property) obj;
		if (metadata == null || metadata.getMergedPropertyType() == null) {
			if (other.metadata != null && other.metadata.getMergedPropertyType() != null)
				return false;
		} else if (!metadata.getMergedPropertyType().equals(other.metadata.getMergedPropertyType()))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
