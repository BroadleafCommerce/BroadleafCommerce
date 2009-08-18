package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	import org.broadleafcommerce.admin.core.vo.security.AdminUser;

	public class DeleteAdminUserEvent extends CairngormEvent
	{
		public static const EVENT_DELETE_ADMIN_USER:String = "delete_admin_user";
		public var adminUser:AdminUser;

		public function DeleteAdminUserEvent(adminUser:AdminUser)
		{
			super(EVENT_DELETE_ADMIN_USER);
			this.adminUser = adminUser;
		}

	}
}