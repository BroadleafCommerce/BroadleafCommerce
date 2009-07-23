package org.broadleafcommerce.admin.core.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class AddModulesToViewEvent extends CairngormEvent
	{
		public static const EVENT_ADD_MODULES_TO_VIEW:String = "add_modules_to_view_event";
		
		public function AddModulesToViewEvent()
		{
			super(EVENT_ADD_MODULES_TO_VIEW);
		}
		
	}
}