package org.broadleafcommerce.admin.catalog.commands.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.BuildCatalogEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	
	public class FindAllCatalogSkusCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void{
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.findAllSkus();
		}
		
		public function result(data:Object):void{
			var event:ResultEvent = ResultEvent(data);
			CatalogModelLocator.getInstance().skuModel.catalogSkus = ArrayCollection(event.result);
			var bcte:BuildCatalogEvent = new BuildCatalogEvent();
			bcte.dispatch()
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}
	}
}