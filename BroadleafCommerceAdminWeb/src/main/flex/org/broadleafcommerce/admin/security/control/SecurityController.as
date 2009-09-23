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
package org.broadleafcommerce.admin.security.control
{
	import com.adobe.cairngorm.control.FrontController;

	import org.broadleafcommerce.admin.security.commands.admins.DeleteAdminUserCommand;
	import org.broadleafcommerce.admin.security.commands.admins.FindAllAdminUsersCommand;
	import org.broadleafcommerce.admin.security.commands.admins.SaveAdminUserCommand;
	import org.broadleafcommerce.admin.security.commands.admins.ViewAdminsCommand;
	import org.broadleafcommerce.admin.security.commands.permissions.DeleteAdminPermissionCommand;
	import org.broadleafcommerce.admin.security.commands.permissions.FindAllPermissionsCommand;
	import org.broadleafcommerce.admin.security.commands.permissions.SaveAdminPermissionCommand;
	import org.broadleafcommerce.admin.security.commands.permissions.ViewPermissionsCommand;
	import org.broadleafcommerce.admin.security.commands.roles.DeleteAdminRoleCommand;
	import org.broadleafcommerce.admin.security.commands.roles.FindAllRolesCommand;
	import org.broadleafcommerce.admin.security.commands.roles.SaveRoleCommand;
	import org.broadleafcommerce.admin.security.commands.roles.ViewRolesCommand;
	import org.broadleafcommerce.admin.security.control.events.DeleteAdminPermissionEvent;
	import org.broadleafcommerce.admin.security.control.events.DeleteAdminRoleEvent;
	import org.broadleafcommerce.admin.security.control.events.DeleteAdminUserEvent;
	import org.broadleafcommerce.admin.security.control.events.FindAllAdminUsersEvent;
	import org.broadleafcommerce.admin.security.control.events.FindAllPermissionsEvent;
	import org.broadleafcommerce.admin.security.control.events.FindAllRolesEvent;
	import org.broadleafcommerce.admin.security.control.events.SaveAdminPermissionEvent;
	import org.broadleafcommerce.admin.security.control.events.SaveAdminUserEvent;
	import org.broadleafcommerce.admin.security.control.events.SaveRoleEvent;
	import org.broadleafcommerce.admin.security.control.events.ViewAdminsEvent;
	import org.broadleafcommerce.admin.security.control.events.ViewPermissionsEvent;
	import org.broadleafcommerce.admin.security.control.events.ViewRolesEvent;

	public class SecurityController extends FrontController
	{
		public function SecurityController()
		{
			super();
			addCommand(ViewAdminsEvent.EVENT_VIEW_ADMINS, ViewAdminsCommand);
			addCommand(ViewRolesEvent.EVENT_VIEW_ROLES, ViewRolesCommand);
			addCommand(ViewPermissionsEvent.EVENT_VIEW_PERMISSIONS, ViewPermissionsCommand);
			addCommand(FindAllAdminUsersEvent.EVENT_VIEW_ALL_ADMINS, FindAllAdminUsersCommand);
			addCommand(FindAllRolesEvent.EVENT_VIEW_ALL_ROLES, FindAllRolesCommand);
			addCommand(FindAllPermissionsEvent.EVENT_VIEW_ALL_PERMISSIONS, FindAllPermissionsCommand);
			addCommand(SaveAdminPermissionEvent.EVENT_SAVE_ADMIN_PERMISSION, SaveAdminPermissionCommand);
			addCommand(SaveRoleEvent.EVENT_SAVE_ROLE, SaveRoleCommand);
			addCommand(SaveAdminUserEvent.EVENT_SAVE_ADMIN_USER, SaveAdminUserCommand);
			addCommand(DeleteAdminPermissionEvent.EVENT_DELETE_ADMIN_PERMISSION, DeleteAdminPermissionCommand);
			addCommand(DeleteAdminRoleEvent.EVENT_DELETE_ADMIN_ROLE, DeleteAdminRoleCommand);
			addCommand(DeleteAdminUserEvent.EVENT_DELETE_ADMIN_USER, DeleteAdminUserCommand);

		}

	}
}