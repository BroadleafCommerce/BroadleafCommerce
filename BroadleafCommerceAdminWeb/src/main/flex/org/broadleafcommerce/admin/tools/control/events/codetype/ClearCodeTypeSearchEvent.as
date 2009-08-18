package org.broadleafcommerce.admin.tools.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class ClearCodeTypeSearchEvent extends CairngormEvent
	{
		
		public static const EVENT_CLEAR_CODE_TYPE_SEARCH:String = "event_clear_code_type_seach";
		
		public function ClearCodeTypeSearchEvent()
		{
			super(EVENT_CLEAR_CODE_TYPE_SEARCH);
		}

	}
}