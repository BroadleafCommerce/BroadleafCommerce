package org.broadleafcommerce.gwt.client.datasource;

import java.io.Serializable;

public class JoinTable implements Serializable {

	private static final long serialVersionUID = 1L;

	private String manyToField;
	private String associationPathToLinkedObject;
	private String linkedProperty;
	private String associationPathToTargetObject;
	private String joinTableEntityClassname;
	
	public JoinTable() {
		//do nothing
	}
	
	public JoinTable(String manyToField, String associationPathToLinkedObject, String linkedProperty, String associatedPathToTargetObject, String joinTableEntityClassname) {
		this.manyToField = manyToField;
		this.associationPathToLinkedObject = associationPathToLinkedObject;
		this.linkedProperty = linkedProperty;
		this.associationPathToTargetObject = associatedPathToTargetObject;
		this.joinTableEntityClassname = joinTableEntityClassname;
	}
	
	public String getManyToField() {
		return manyToField;
	}
	
	public void setManyToField(String manyToField) {
		this.manyToField = manyToField;
	}
	
	public String getAssociationPathToLinkedObject() {
		return associationPathToLinkedObject;
	}
	
	public void setAssociationPathToLinkedObject(String associationPath) {
		this.associationPathToLinkedObject = associationPath;
	}

	public String getLinkedProperty() {
		return linkedProperty;
	}

	public void setLinkedProperty(String targetProperty) {
		this.linkedProperty = targetProperty;
	}

	public String getAssociationPathToTargetObject() {
		return associationPathToTargetObject;
	}

	public void setAssociationPathToTargetObject(String associationPathToTargetObject) {
		this.associationPathToTargetObject = associationPathToTargetObject;
	}

	public String getJoinTableEntityClassname() {
		return joinTableEntityClassname;
	}

	public void setJoinTableEntityClassname(String joinTableEntityClassname) {
		this.joinTableEntityClassname = joinTableEntityClassname;
	}
	
}
