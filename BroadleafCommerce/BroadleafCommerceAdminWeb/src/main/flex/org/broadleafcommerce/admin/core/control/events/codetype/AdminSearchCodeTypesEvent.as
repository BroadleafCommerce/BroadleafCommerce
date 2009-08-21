package org.broadleafcommerce.admin.core.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class AdminSearchCodeTypesEvent extends CairngormEvent
	{
		
		public static const EVENT_SEARCH_CODE_TYPES:String = "event_search_code_types_admin";
		
		public var keyword:String;
		
		public function AdminSearchCodeTypesEvent(keyword:String)
		{
			super(EVENT_SEARCH_CODE_TYPES);
			this.keyword = keyword;
		}

	}
}