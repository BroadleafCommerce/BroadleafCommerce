package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	import org.broadleafcommerce.admin.security.vo.AdminPermission;

	public class SaveAdminPermissionEvent extends CairngormEvent
	{
		public static const EVENT_SAVE_ADMIN_PERMISSION:String = "save_admin_permission_event";
		public var permission:AdminPermission;

		public function SaveAdminPermissionEvent(permission:AdminPermission)
		{
			super(EVENT_SAVE_ADMIN_PERMISSION);
			this.permission = permission;
		}

	}
}