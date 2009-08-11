package org.broadleafcommerce.admin.tools.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class FindAllCodeTypesEvent extends CairngormEvent
	{
		public static const EVENT_FIND_ALL_CODE_TYPES:String = "event_find_all_code_types";
		
		public function FindAllCodeTypesEvent() 
		{
			super(EVENT_FIND_ALL_CODE_TYPES);
		}

	}
}