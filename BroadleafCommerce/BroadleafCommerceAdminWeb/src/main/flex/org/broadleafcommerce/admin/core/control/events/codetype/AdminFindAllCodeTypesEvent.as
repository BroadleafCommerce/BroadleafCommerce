package org.broadleafcommerce.admin.core.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class AdminFindAllCodeTypesEvent extends CairngormEvent
	{
		public static const EVENT_FIND_ALL_CODE_TYPES:String = "event_find_all_code_types_admin";
		
		public function AdminFindAllCodeTypesEvent() 
		{
			super(EVENT_FIND_ALL_CODE_TYPES);
		}

	}
}