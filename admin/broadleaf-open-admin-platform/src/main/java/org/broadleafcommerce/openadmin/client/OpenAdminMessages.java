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
    public String userPermissionsTitle();
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
    public String baseLocale();
    public String baseAdminUser();
    public String baseAdminRole();
    public String baseAdminPermission();
    public String searchForPermission();
    public String openAdminVersion();
    public String buildDate();
    public String clientBuildDate();
    
    // Toolbar operations
    public String addTitle();
    public String saveTitle();
    public String removeTitle();
    public String restoreTitle();
    public String restoreTooltip();

    // Sandbox Operations
    public String promoteTitle();
    public String promoteTooltip();

    public String promoteAllTitle();
    public String promoteAllTooltip();

    public String revertTitle();
    public String revertTooltip();

    public String revertAllTitle();
    public String revertAllTooltip();

    public String rejectTitle();
    public String rejectTooltip();

    public String rejectAllTitle();
    public String rejectAllTooltip();

    public String unlockTitle();
    public String unlockTooltip();

    public String unlockAllTitle();
    public String unlockAllTooltip();

    public String reclaimTitle();
    public String reclaimTooltip();

    public String reclaimAllTitle();
    public String reclaimAllTooltip();

    public String previewTitle();
    public String refreshTitle();

    public String selectPolymorphicType();
    public String viewFullSize();

    public String ok();
    public String cancel();
    public String noModulesAuthorized();
    public String noAuthorizedPages();
    public String insufficientPrivileges();

    public String urlStructurePreProcessTitle();
    public String userSecurityPreProcessTitle();
    public String workflowEnabledPreProcessTitle();
    public String ejb3ConfigurationPreProcessTitle();
    public String resetDataTitle();
    public String lockedMessage();
    public String dirtyMessage();
    public String inActiveMessage();
    public String deletedMessage();
}
