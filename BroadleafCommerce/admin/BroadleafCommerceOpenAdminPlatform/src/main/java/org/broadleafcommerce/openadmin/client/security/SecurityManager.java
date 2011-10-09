/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.openadmin.client.security;

import java.util.*;

/**
 * 
 * @author jfischer
 *
 */
public class SecurityManager {
	
	private static SecurityManager manager = null;

	public static AdminUser USER;

	private Map<String, List<String>> permissionSecuredSections = new HashMap<String, List<String>>();
    private Map<String, HashSet<String>> moduleSectionList = new HashMap<String, HashSet<String>>();
	private Map<String, String> securedFields = new HashMap<String, String>();

	public static SecurityManager getInstance() {
		if (manager == null) {
			SecurityManager.manager = new SecurityManager();
		}
		return SecurityManager.manager;
	}
	
	public void registerSection(String moduleKey, String sectionViewKey, List<String> permissions){
		permissionSecuredSections.put(sectionViewKey, permissions);
        HashSet<String> currentSections = moduleSectionList.get(moduleKey);
        if (currentSections == null) {
            currentSections = new HashSet<String>();
            moduleSectionList.put(moduleKey, currentSections);
        }
        currentSections.add(sectionViewKey);
	}
	
	public void registerField(String fieldName, String securityLevel){
		securedFields.put(fieldName, securityLevel);
	}
	
	public boolean isUserAuthorizedToViewSection(String sectionViewKey){
		List<String> authorizedPermissions = permissionSecuredSections.get(sectionViewKey);
		
		for (String permission : USER.getPermissions()){
			if (authorizedPermissions != null && authorizedPermissions.contains(permission)){
				return true;
			}
		}
		
		return false;
	}

    public boolean isUserAuthorizedToViewModule(String moduleKey) {
        Set moduleSections = moduleSectionList.get(moduleKey);
        if (moduleSections != null) {
            for (Iterator<String> iterator = moduleSections.iterator(); iterator.hasNext(); ) {
                String sectionKey =  iterator.next();
                if (isUserAuthorizedToViewSection(sectionKey)) {
                    return true;
                }
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
