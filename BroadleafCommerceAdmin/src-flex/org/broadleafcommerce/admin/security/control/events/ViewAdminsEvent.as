package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class ViewAdminsEvent extends CairngormEvent
	{
		public static const EVENT_VIEW_ADMINS:String = "view_admins_event";

		public function ViewAdminsEvent()
		{
			super(EVENT_VIEW_ADMINS);
		}

	}
}