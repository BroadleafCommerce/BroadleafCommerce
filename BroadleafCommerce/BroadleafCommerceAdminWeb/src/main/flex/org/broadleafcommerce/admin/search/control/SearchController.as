package org.broadleafcommerce.admin.search.control
{
	import com.adobe.cairngorm.control.FrontController;
	import org.broadleafcommerce.admin.search.commands.CreateSearchInterceptCommand;
	import org.broadleafcommerce.admin.search.commands.DeleteSearchInterceptCommand;
	import org.broadleafcommerce.admin.search.commands.FindAllSearchInterceptsCommand;
	import org.broadleafcommerce.admin.search.commands.UpdateSearchInterceptCommand;
	import org.broadleafcommerce.admin.search.control.events.CreateSearchInterceptEvent;
	import org.broadleafcommerce.admin.search.control.events.DeleteSearchInterceptEvent;
	import org.broadleafcommerce.admin.search.control.events.FindAllSearchInterceptsEvent;
	import org.broadleafcommerce.admin.search.control.events.UpdateSearchInterceptEvent;
	
	public class SearchController extends FrontController
	{
		public function SearchController()
		{
			super();
			addCommand(FindAllSearchInterceptsEvent.EVENT_FIND_ALL_SEARCH_INTERCEPTS, FindAllSearchInterceptsCommand);
			addCommand(CreateSearchInterceptEvent.EVENT_CREATE_SEARCH_INTERCEPT, CreateSearchInterceptCommand);
			addCommand(UpdateSearchInterceptEvent.EVENT_UPDATE_SEARCH_INTERCEPT, UpdateSearchInterceptCommand);
			addCommand(DeleteSearchInterceptEvent.EVENT_DELETE_SEARCH_INTERCEPT, DeleteSearchInterceptCommand);
		}

	}
}