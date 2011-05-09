package org.broadleafcommerce.gwt.server.dao;

import java.io.Serializable;

import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.presentation.SupportedFieldType;


public class FieldMetadata implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private SupportedFieldType fieldType;
	private Integer length;
	private Boolean required;
	private Boolean unique;
	private Integer scale;
	private Integer precision;
	private Boolean mutable;
	private String inheritedFromType;
	private String availableToTypes;
	private String complexIdProperty;
	private String providedForeignKeyClass;
	private Boolean collection;
	private MergedPropertyType mergedPropertyType;
	
	private FieldPresentationAttributes presentationAttributes;
	
	public SupportedFieldType getFieldType() {
		return fieldType;
	}
	
	public void setFieldType(SupportedFieldType fieldType) {
		this.fieldType = fieldType;
	}
	
	public Integer getLength() {
		return length;
	}
	
	public void setLength(Integer length) {
		this.length = length;
	}
	
	public Boolean getRequired() {
		return required;
	}
	
	public void setRequired(Boolean required) {
		this.required = required;
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

	public Boolean getUnique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public Boolean getMutable() {
		return mutable;
	}

	public void setMutable(Boolean mutable) {
		this.mutable = mutable;
	}

	public String getAvailableToTypes() {
		return availableToTypes;
	}

	public void setAvailableToTypes(String directType) {
		this.availableToTypes = directType;
	}

	/*public String getComplexType() {
		return complexType;
	}

	public void setComplexType(String complexType) {
		this.complexType = complexType;
	}*/

	public String getComplexIdProperty() {
		return complexIdProperty;
	}

	public void setComplexIdProperty(String complexId) {
		this.complexIdProperty = complexId;
	}

	public String getInheritedFromType() {
		return inheritedFromType;
	}

	public void setInheritedFromType(String inheritedFromType) {
		this.inheritedFromType = inheritedFromType;
	}

	public String getProvidedForeignKeyClass() {
		return providedForeignKeyClass;
	}

	public void setProvidedForeignKeyClass(String providedForeignKeyClass) {
		this.providedForeignKeyClass = providedForeignKeyClass;
	}

	public FieldPresentationAttributes getPresentationAttributes() {
		return presentationAttributes;
	}

	public void setPresentationAttributes(FieldPresentationAttributes presentationAttributes) {
		this.presentationAttributes = presentationAttributes;
	}

	public Boolean getCollection() {
		return collection;
	}

	public void setCollection(Boolean collection) {
		this.collection = collection;
	}

	public MergedPropertyType getMergedPropertyType() {
		return mergedPropertyType;
	}

	public void setMergedPropertyType(MergedPropertyType mergedPropertyType) {
		this.mergedPropertyType = mergedPropertyType;
	}

}
