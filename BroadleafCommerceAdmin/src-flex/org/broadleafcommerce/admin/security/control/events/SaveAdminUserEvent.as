package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	import org.broadleafcommerce.admin.core.vo.security.AdminUser;

	public class SaveAdminUserEvent extends CairngormEvent
	{
		public static const EVENT_SAVE_ADMIN_USER:String = "save_admin_user_event";
		public var adminUser:AdminUser;

		public function SaveAdminUserEvent(adminUser:AdminUser)
		{
			super(EVENT_SAVE_ADMIN_USER);
			this.adminUser = adminUser;
		}

	}
}