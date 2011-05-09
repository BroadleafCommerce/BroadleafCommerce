package org.broadleafcommerce.gwt.client.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityManager {
	
	private static SecurityManager manager = null;

	public static AdminUser USER;
	
	private Map<String, List<String>> roleSecuredSections = new HashMap<String, List<String>>();
	private Map<String, List<String>> permissionSecuredSections = new HashMap<String, List<String>>();
	private Map<String, String> securedFields = new HashMap<String, String>();

	public static SecurityManager getInstance() {
		if (manager == null) {
			SecurityManager.manager = new SecurityManager();
		}
		return SecurityManager.manager;
	}
	
	public void registerSection(String sectionViewKey, List<String> roles, List<String> permissions){
		roleSecuredSections.put(sectionViewKey, roles);
		permissionSecuredSections.put(sectionViewKey, permissions);
	}
	
	public void registerField(String fieldName, String securityLevel){
		securedFields.put(fieldName, securityLevel);
	}
	
	public boolean isUserAuthorizedToViewSection(String sectionViewKey){
		List<String> authorizedRoles = roleSecuredSections.get(sectionViewKey);
		List<String> authorizedPermissions = permissionSecuredSections.get(sectionViewKey);
		for (String role : USER.getRoles()){
			if (authorizedRoles != null && authorizedRoles.contains(role)){
				return true;
			}
		}
		
		for (String permission : USER.getPermissions()){
			if (authorizedPermissions != null && authorizedPermissions.contains(permission)){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isUserAuthorizedToEditField(String fieldName){
		String securityLevel = securedFields.get(fieldName);
		for (String permission : USER.getPermissions()){
			if (securityLevel != null && securityLevel.equals(permission)){
				return true;
			}
		}
		
		return false;
	}
	
	public void doSecure(String permission, SecureCallbackAdapter adapter){
		if (USER.getPermissions() !=null && permission!=null && USER.getPermissions().contains(permission)){
			adapter.succeed();
		} else {
			adapter.fail();
		}
	}
}
