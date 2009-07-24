package org.broadleafcommerce.admin.security.control
{
	import com.adobe.cairngorm.control.FrontController;

	import org.broadleafcommerce.admin.security.commands.admins.FindAllAdminUsersCommand;
	import org.broadleafcommerce.admin.security.commands.permissions.FindAllPermissionsCommand;
	import org.broadleafcommerce.admin.security.commands.roles.FindAllRolesCommand;
	import org.broadleafcommerce.admin.security.commands.admins.ViewAdminsCommand;
	import org.broadleafcommerce.admin.security.commands.permissions.ViewPermissionsCommand;
	import org.broadleafcommerce.admin.security.commands.roles.ViewRolesCommand;
	import org.broadleafcommerce.admin.security.control.events.FindAllAdminUsersEvent;
	import org.broadleafcommerce.admin.security.control.events.FindAllPermissionsEvent;
	import org.broadleafcommerce.admin.security.control.events.FindAllRolesEvent;
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
		}

	}
}