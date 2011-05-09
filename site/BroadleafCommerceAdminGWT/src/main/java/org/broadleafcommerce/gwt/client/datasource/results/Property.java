package org.broadleafcommerce.gwt.client.datasource.results;

import java.io.Serializable;

public class Property implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String type;
	private Boolean unique;
	private Boolean required;
	private Long length;
	private Integer scale;
	private Integer precision;
	private Boolean mutable;
	private String inheritedFromType;
	private String value;
	private String availableToTypes;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Boolean getUnique() {
		return unique;
	}
	
	public void setUnique(Boolean unique) {
		this.unique = unique;
	}
	
	public Boolean getRequired() {
		return required;
	}
	
	public void setRequired(Boolean required) {
		this.required = required;
	}
	
	public Long getLength() {
		return length;
	}
	
	public void setLength(Long length) {
		this.length = length;
	}
	
	public Integer getScale() {
		return scale;
	}
	
	public void setScale(Integer scale) {
		this.scale = scale;
	}
	
	public Integer getPrecision() {
		return precision;
	}
	
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}
	
	public Boolean getMutable() {
		return mutable;
	}
	
	public void setMutable(Boolean mutable) {
		this.mutable = mutable;
	}
	
	public String getInheritedFromType() {
		return inheritedFromType;
	}
	
	public void setInheritedFromType(String inheritedFromType) {
		this.inheritedFromType = inheritedFromType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAvailableToTypes() {
		return availableToTypes;
	}

	public void setAvailableToTypes(String availableToTypes) {
		this.availableToTypes = availableToTypes;
	}

}
