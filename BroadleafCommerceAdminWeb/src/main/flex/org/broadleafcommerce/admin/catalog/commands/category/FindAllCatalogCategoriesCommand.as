package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.BuildCatalogChainEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;

	public class FindAllCatalogCategoriesCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: FindAllCategoriesCommand.execute()");
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.findAllCategories();
		}
		
		public function result(data:Object):void
		{
			trace("DEBUG: FindAllCategoriesCommand.result()");
			var event:ResultEvent = ResultEvent(data);
			CatalogModelLocator.getInstance().categoryModel.categoryArray = ArrayCollection(event.result);
			var bcte:BuildCatalogChainEvent = new BuildCatalogChainEvent();
			bcte.dispatch()
		}
		
		public function fault(info:Object):void
		{
			trace("DEBUG: FindAllCategoriesCommand.fault()");			
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}
		
	}
}