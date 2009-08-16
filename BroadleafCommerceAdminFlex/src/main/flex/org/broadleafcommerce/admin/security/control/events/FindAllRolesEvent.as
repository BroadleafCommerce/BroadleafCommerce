package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class FindAllRolesEvent extends CairngormEvent
	{
		public static const EVENT_VIEW_ALL_ROLES:String = "view_all_roles_event";

		public function FindAllRolesEvent()
		{
			super(EVENT_VIEW_ALL_ROLES);
		}

	}
}