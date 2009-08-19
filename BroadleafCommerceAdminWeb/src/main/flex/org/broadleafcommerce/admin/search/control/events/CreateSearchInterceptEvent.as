package org.broadleafcommerce.admin.search.control.events
{
	import org.broadleafcommerce.admin.search.vo.SearchIntercept;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	
	public class CreateSearchInterceptEvent extends CairngormEvent
	{
		
		public var searchIntercept:SearchIntercept;
		public static const EVENT_CREATE_SEARCH_INTERCEPT:String = "event_create_search_intercept";
		
		public function CreateSearchInterceptEvent(searchIntercept:SearchIntercept)
		{
			super(EVENT_CREATE_SEARCH_INTERCEPT);
			this.searchIntercept = searchIntercept;
		}
		
	}
}