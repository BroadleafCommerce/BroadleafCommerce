package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class FindAdminUserByIdEvent extends CairngormEvent
	{
		public static const EVENT_VIEW_ADMIN_BY_ID:String = "view_admin_by_id_event";
		public var id:int;

		public function FindAdminUserByIdEvent(id:int)
		{
			super(EVENT_VIEW_ADMIN_BY_ID);
			this.id = id;
		}

	}
}