package org.broadleafcommerce.admin.tools.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class SearchCodeTypesEvent extends CairngormEvent
	{
		
		public static const EVENT_SEARCH_CODE_TYPES:String = "event_search_code_types";
		
		public var keyword:String;
		
		public function SearchCodeTypesEvent(keyword:String)
		{
			super(EVENT_SEARCH_CODE_TYPES);
			this.keyword = keyword;
		}

	}
}