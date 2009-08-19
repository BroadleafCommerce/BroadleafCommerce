package org.broadleafcommerce.admin.search.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class FindAllSearchInterceptsEvent extends CairngormEvent
	{
		
		public static const EVENT_FIND_ALL_SEARCH_INTERCEPTS:String = "event_find_all_search_intercepts";
		
		public function FindAllSearchInterceptsEvent()
		{
			super(EVENT_FIND_ALL_SEARCH_INTERCEPTS);
		}
		
	}
}