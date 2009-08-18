package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class ViewPermissionsEvent extends CairngormEvent
	{
		public static const EVENT_VIEW_PERMISSIONS:String = "view_permissions_event";

		public function ViewPermissionsEvent()
		{
			super(EVENT_VIEW_PERMISSIONS);
		}

	}
}