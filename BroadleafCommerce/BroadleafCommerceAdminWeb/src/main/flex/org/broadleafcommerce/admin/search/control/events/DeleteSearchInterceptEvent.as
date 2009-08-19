package org.broadleafcommerce.admin.search.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import org.broadleafcommerce.admin.search.vo.SearchIntercept;
	
	public class DeleteSearchInterceptEvent extends CairngormEvent
	{
		
		public var searchIntercept:SearchIntercept;
		public static const EVENT_DELETE_SEARCH_INTERCEPT:String = "event_delete_search_intercept";
		
		public function DeleteSearchInterceptEvent(searchIntercept:SearchIntercept)
		{
			super(EVENT_DELETE_SEARCH_INTERCEPT);
			this.searchIntercept = searchIntercept;
		}
		
	}
}