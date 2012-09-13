/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.security;


import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author jfischer
 *
 */
public class SecurityManager {

    private static final RegExp PATTERN = RegExp.compile("_");
	private static SecurityManager MANAGER;

	public static AdminUser USER;

	private Map<String, List<String>> permissionSecuredSections = new HashMap<String, List<String>>(20);
    private Map<String, HashSet<String>> moduleSectionList = new HashMap<String, HashSet<String>>(20);
	private Map<String, String> securedFields = new HashMap<String, String>(20);

	public static SecurityManager getInstance() {
		if (MANAGER == null) {
			MANAGER = new SecurityManager();
		}
		return MANAGER;
	}
	
	public void registerSection(String moduleKey, String sectionViewKey, List<String> permissions){
		permissionSecuredSections.put(sectionViewKey, permissions);
        HashSet<String> currentSections = moduleSectionList.get(moduleKey);
        if (currentSections == null) {
            currentSections = new HashSet<String>(20);
            moduleSectionList.put(moduleKey, currentSections);
        }
        currentSections.add(sectionViewKey);
	}
	
	public void registerField(String fieldName, String securityLevel){
		securedFields.put(fieldName, securityLevel);
	}

    protected String parseForAllPermission(String currentPermission) {
        SplitResult pieces = PATTERN.split(currentPermission);
        StringBuilder builder = new StringBuilder(50);
        builder.append(pieces.get(0));
        builder.append("_ALL_");
        for (int j = 2; j<pieces.length(); j++) {
            builder.append(pieces.get(j));
            if (j < pieces.length() - 1) {
                builder.append('_');
            }
        }

        return builder.toString();
    }
	
	public boolean isUserAuthorizedToViewSection(String sectionViewKey){
		List<String> authorizedPermissions = permissionSecuredSections.get(sectionViewKey);
		for (String permission : USER.getPermissions()){
            if (authorizedPermissions != null) {
                if (authorizedPermissions.contains(permission)){
                    return true;
                }
                for (String authorizedPermission : authorizedPermissions) {
                    if (permission.equals(parseForAllPermission(authorizedPermission))) {
                        return true;
                    }
                }
            }
		}
		
		return false;
	}

    public boolean isUserAuthorizedToViewModule(String moduleKey) {
        Set<String> moduleSections = moduleSectionList.get(moduleKey);
        if (moduleSections != null) {
            for (String sectionKey : moduleSections) {
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
			if (securityLevel != null && (securityLevel.equals(permission) || permission.equals(parseForAllPermission(securityLevel)))){
				return true;
			}
		}
		
		return false;
	}
	
	public void doSecure(String permission, SecureCallbackAdapter adapter){
		if (USER.getPermissions() !=null && permission!=null && (USER.getPermissions().contains(permission) || USER.getPermissions().contains(parseForAllPermission(permission)))){
			adapter.succeed();
		} else {
			adapter.fail();
		}
	}
}
