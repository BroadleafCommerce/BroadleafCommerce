package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class FindAllAdminUsersEvent extends CairngormEvent
	{
		public static const EVENT_VIEW_ALL_ADMINS:String = "view_all_admins_event";

		public function FindAllAdminUsersEvent()
		{
			super(EVENT_VIEW_ALL_ADMINS);
		}

	}
}