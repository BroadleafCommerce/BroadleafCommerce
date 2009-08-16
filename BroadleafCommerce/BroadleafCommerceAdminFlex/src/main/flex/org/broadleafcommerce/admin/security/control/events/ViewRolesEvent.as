package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class ViewRolesEvent extends CairngormEvent
	{
		public static const EVENT_VIEW_ROLES:String = "view_roles_event";

		public function ViewRolesEvent()
		{
			super(EVENT_VIEW_ROLES);
		}

	}
}