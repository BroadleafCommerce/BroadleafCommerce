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
package org.broadleafcommerce.openadmin.client;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;

/**
 * 
 * @author jfischer
 *
 */
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
@DefaultLocale("en_US")
public interface OpenAdminMessages extends ConstantsWithLookup {

	public String contactingServerTitle();
	public String currentUser();
	public String logout();
	public String emptyMessage();
    public String userManagementMainTitle();
	public String userListTitle();
	public String userDetailsTitle();
	public String userRolesTitle();
	public String newAdminUserTitle();
	public String clonePromotionHelp();
	public String orderItemCombineLabel();
	public String confirmResetPassword();
	public String resetPasswordSuccessful();
	public String userAdminModuleTitle();
	public String blcProjectPage();
	public String rolesTitle();
	public String roleListTitle();
	public String roleDetailsTitle();
	public String roleManagementMainTitle();
	public String roleName();
	public String newRoleTitle();
    public String permissionListTitle();
    public String permissionDetailsTitle();
    public String permissionManagementMainTitle();
    public String newPermissionTitle();
    public String newItemTitle();
}
