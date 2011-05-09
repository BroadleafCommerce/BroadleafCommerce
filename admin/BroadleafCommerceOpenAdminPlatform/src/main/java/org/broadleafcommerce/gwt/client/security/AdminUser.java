package org.broadleafcommerce.gwt.client.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdminUser implements Serializable {

	private static final long serialVersionUID = 1L;

	protected List<String> roles = new ArrayList<String>();
	protected List<String> permissions = new ArrayList<String>();
	
	public List<String> getRoles() {
		return roles;
	}
	
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	public List<String> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
	
}
