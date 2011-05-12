package org.broadleafcommerce.gwt.security;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityOperationType;

public class SecurityConfig {

	private String ceilingEntityFullyQualifiedName;
	private List<EntityOperationType> requiredTypes;
	private List<String> permissions = new ArrayList<String>();
	private List<String> roles = new ArrayList<String>();
	
	public String getCeilingEntityFullyQualifiedName() {
		return ceilingEntityFullyQualifiedName;
	}
	
	public void setCeilingEntityFullyQualifiedName(
			String ceilingEntityFullyQualifiedName) {
		this.ceilingEntityFullyQualifiedName = ceilingEntityFullyQualifiedName;
	}
	
	public List<EntityOperationType> getRequiredTypes() {
		return requiredTypes;
	}
	
	public void setRequiredTypes(List<EntityOperationType> requiredTypes) {
		this.requiredTypes = requiredTypes;
	}
	
	public List<String> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}
