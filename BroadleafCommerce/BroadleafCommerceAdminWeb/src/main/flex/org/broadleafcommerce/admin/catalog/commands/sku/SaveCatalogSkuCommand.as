package org.broadleafcommerce.admin.catalog.commands.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.sku.FindAllSkusEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.SaveSkuEvent;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;
	
	public class SaveCatalogSkuCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void{
			var scse:SaveSkuEvent = SaveSkuEvent(event);
			var sku:Sku = scse.sku;
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.saveSku(sku);				
		}
		
		public function result(data:Object):void{
//			var facse:FindAllSkusEvent = new FindAllSkusEvent();
//			facse.dispatch();
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}
	}
}