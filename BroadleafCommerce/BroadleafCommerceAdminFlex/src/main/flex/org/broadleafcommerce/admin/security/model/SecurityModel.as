package org.broadleafcommerce.admin.security.model
{
	[Bindable]
	public class SecurityModel
	{
		public function SecurityModel()
		{
		}
		public static const SERVICE_ID:String = "blAdminSecurityService";

		public static const STATE_VIEW_ADMINS:String = "view_admins_state";
		public static const STATE_VIEW_ROLES:String = "view_roles_state";
		public static const STATE_VIEW_PERMISSIONS:String = "view_permissions_state";

		public var viewState:String = STATE_VIEW_ADMINS;
	}
}