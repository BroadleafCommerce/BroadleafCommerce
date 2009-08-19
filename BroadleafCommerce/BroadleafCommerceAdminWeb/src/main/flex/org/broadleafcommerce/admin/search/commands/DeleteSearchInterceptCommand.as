package org.broadleafcommerce.admin.search.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	
	import org.broadleafcommerce.admin.search.business.SearchServiceDelegate;
	import org.broadleafcommerce.admin.search.control.events.DeleteSearchInterceptEvent;
	import org.broadleafcommerce.admin.search.control.events.FindAllSearchInterceptsEvent;
	import org.broadleafcommerce.admin.search.model.SearchModel;
	import org.broadleafcommerce.admin.search.model.SearchModelLocator;
	
	public class DeleteSearchInterceptCommand implements Command, IResponder
	{
		private var searchModel:SearchModel = SearchModelLocator.getInstance().searchModel;
		
		public function execute(event:CairngormEvent):void
		{
			var dsie:DeleteSearchInterceptEvent = event as DeleteSearchInterceptEvent;
			var delegate:SearchServiceDelegate = new SearchServiceDelegate(this);
			delegate.deleteSearchIntercept(dsie.searchIntercept);
		}
				
		public function result(data:Object):void
		{
			var reloadIntercepts:FindAllSearchInterceptsEvent = new FindAllSearchInterceptsEvent();
			reloadIntercepts.dispatch();
		}
		
		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+event);			
		}
		
	}
}