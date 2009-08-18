package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class FindAllPermissionsEvent extends CairngormEvent
	{
		public static const EVENT_VIEW_ALL_PERMISSIONS:String = "view_all_permissions_event";

		public function FindAllPermissionsEvent()
		{
			super(EVENT_VIEW_ALL_PERMISSIONS);
		}

	}
}