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

package org.broadleafcommerce.openadmin.client;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class UserManagementModule extends AbstractModule {
	
	public void onModuleLoad() {
		setModuleTitle(BLCMain.getMessageManager().getString("userAdminModuleTitle"));
		setModuleKey("BLCOpenAdmin");
		
		List<String> userManagementPermissions = new ArrayList<String>();
		userManagementPermissions.add("PERMISSION_CREATE_ADMIN_USER");
        userManagementPermissions.add("PERMISSION_UPDATE_ADMIN_USER");
        userManagementPermissions.add("PERMISSION_DELETE_ADMIN_USER");
        userManagementPermissions.add("PERMISSION_READ_ADMIN_USER");
        
		setSection(
            BLCMain.getMessageManager().getString("userManagementMainTitle"),
			"user",
			"org.broadleafcommerce.openadmin.client.view.user.UserManagementView",
			"userPresenter",
			"org.broadleafcommerce.openadmin.client.presenter.user.UserManagementPresenter",
			userManagementPermissions
		);

        setSection(
            BLCMain.getMessageManager().getString("roleManagementMainTitle"),
			"role",
			"org.broadleafcommerce.openadmin.client.view.user.RoleManagementView",
			"rolePresenter",
			"org.broadleafcommerce.openadmin.client.presenter.user.RoleManagementPresenter",
			userManagementPermissions
		);
		
        setSection(
            BLCMain.getMessageManager().getString("permissionManagementMainTitle"),
			"permission",
			"org.broadleafcommerce.openadmin.client.view.user.PermissionManagementView",
			"permissionPresenter",
			"org.broadleafcommerce.openadmin.client.presenter.user.PermissionManagementPresenter",
			userManagementPermissions
		);

        setOrder(250);
		
		registerModule();
	}
}