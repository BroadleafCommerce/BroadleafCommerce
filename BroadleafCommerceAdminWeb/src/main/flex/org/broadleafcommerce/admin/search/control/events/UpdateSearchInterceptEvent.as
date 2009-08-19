package org.broadleafcommerce.admin.search.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.search.vo.SearchIntercept;
	
	public class UpdateSearchInterceptEvent extends CairngormEvent
	{
		
		public var searchIntercept:SearchIntercept;
		public static const EVENT_UPDATE_SEARCH_INTERCEPT:String = "event_update_search_intercept";
		
		public function UpdateSearchInterceptEvent(searchIntercept:SearchIntercept)
		{
			super(EVENT_UPDATE_SEARCH_INTERCEPT);
			this.searchIntercept = searchIntercept;
		}
		
	}
}