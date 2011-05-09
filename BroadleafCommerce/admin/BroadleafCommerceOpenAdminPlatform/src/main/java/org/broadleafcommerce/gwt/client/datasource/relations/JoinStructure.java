package org.broadleafcommerce.gwt.client.datasource.relations;

import java.io.Serializable;

public class JoinStructure implements Serializable, PersistencePerspectiveItem {

	private static final long serialVersionUID = 1L;

	private String name;
	private String linkedObjectPath;
	private String targetObjectPath;
	private String joinStructureEntityClassname;
	private String sortField;
	private Boolean sortAscending;
	private String linkedIdProperty;
	private String targetIdProperty;
	private Boolean inverse = Boolean.FALSE;
	
	public JoinStructure() {
		//do nothing
	}
	
	public JoinStructure(String name, String linkedObjectPath, String linkedIdProperty, String targetObjectPath, String targetIdProperty, String joinStructureEntityClassname) {
		this(name, linkedObjectPath, linkedIdProperty, targetObjectPath, targetIdProperty, joinStructureEntityClassname, null, null);
	}
	
	public JoinStructure(String name, String linkedObjectPath, String linkedIdProperty, String targetObjectPath, String targetIdProperty, String joinStructureEntityClassname, String sortField, Boolean sortAscending) {
		this.name = name;
		this.linkedObjectPath = linkedObjectPath;
		this.targetObjectPath = targetObjectPath;
		this.joinStructureEntityClassname = joinStructureEntityClassname;
		this.sortField = sortField;
		this.sortAscending = sortAscending;
		this.linkedIdProperty = linkedIdProperty;
		this.targetIdProperty = targetIdProperty;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String manyToField) {
		this.name = manyToField;
	}

	public String getLinkedObjectPath() {
		return linkedObjectPath;
	}

	public void setLinkedObjectPath(String linkedPropertyPath) {
		this.linkedObjectPath = linkedPropertyPath;
	}

	public String getTargetObjectPath() {
		return targetObjectPath;
	}

	public void setTargetObjectPath(String targetObjectPath) {
		this.targetObjectPath = targetObjectPath;
	}

	public String getJoinStructureEntityClassname() {
		return joinStructureEntityClassname;
	}

	public void setJoinStructureEntityClassname(String joinStructureEntityClassname) {
		this.joinStructureEntityClassname = joinStructureEntityClassname;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public Boolean getSortAscending() {
		return sortAscending;
	}

	public void setSortAscending(Boolean sortAscending) {
		this.sortAscending = sortAscending;
	}

	public String getLinkedIdProperty() {
		return linkedIdProperty;
	}

	public void setLinkedIdProperty(String linkedIdProperty) {
		this.linkedIdProperty = linkedIdProperty;
	}

	public String getTargetIdProperty() {
		return targetIdProperty;
	}

	public void setTargetIdProperty(String targetIdProperty) {
		this.targetIdProperty = targetIdProperty;
	}

	public Boolean getInverse() {
		return inverse;
	}

	public void setInverse(Boolean inverse) {
		this.inverse = inverse;
	}
	
}
