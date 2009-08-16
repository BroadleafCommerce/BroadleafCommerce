package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	import org.broadleafcommerce.admin.core.vo.security.AdminPermission;

	public class DeleteAdminPermissionEvent extends CairngormEvent
	{
		public static const EVENT_DELETE_ADMIN_PERMISSION:String = "delete_admin_permission";
		public var permission:AdminPermission;

		public function DeleteAdminPermissionEvent(permission:AdminPermission)
		{
			super(EVENT_DELETE_ADMIN_PERMISSION);
			this.permission = permission;
		}

	}
}