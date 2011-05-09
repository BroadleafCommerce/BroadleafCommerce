package org.broadleafcommerce.gwt.client.datasource.relations;

import java.io.Serializable;

public class JoinTable implements Serializable, PersistencePerspectiveItem {

	private static final long serialVersionUID = 1L;

	private String manyToField;
	private String linkedObjectPath;
	private String targetObjectPath;
	private String joinTableEntityClassname;
	private String sortField;
	private Boolean sortAscending;
	private String linkedIdProperty;
	private String targetIdProperty;
	
	public JoinTable() {
		//do nothing
	}
	
	public JoinTable(String manyToField, String linkedObjectPath, String linkedIdProperty, String targetObjectPath, String targetIdProperty, String joinTableEntityClassname) {
		this(manyToField, linkedObjectPath, linkedIdProperty, targetObjectPath, targetIdProperty, joinTableEntityClassname, null, null);
	}
	
	public JoinTable(String manyToField, String linkedObjectPath, String linkedIdProperty, String targetObjectPath, String targetIdProperty, String joinTableEntityClassname, String sortField, Boolean sortAscending) {
		this.manyToField = manyToField;
		this.linkedObjectPath = linkedObjectPath;
		this.targetObjectPath = targetObjectPath;
		this.joinTableEntityClassname = joinTableEntityClassname;
		this.sortField = sortField;
		this.sortAscending = sortAscending;
		this.linkedIdProperty = linkedIdProperty;
		this.targetIdProperty = targetIdProperty;
	}
	
	public String getManyToField() {
		return manyToField;
	}
	
	public void setManyToField(String manyToField) {
		this.manyToField = manyToField;
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

	public String getJoinTableEntityClassname() {
		return joinTableEntityClassname;
	}

	public void setJoinTableEntityClassname(String joinTableEntityClassname) {
		this.joinTableEntityClassname = joinTableEntityClassname;
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
	
}
