package org.broadleafcommerce.admin.control.commands.catalog.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.sku.FindAllCatalogSkusEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.SaveCatalogSkuEvent;
	import org.broadleafcommerce.admin.model.business.BroadleafCommerceAdminServiceDelegate;
	import org.broadleafcommerce.admin.model.data.remote.catalog.sku.Sku;
	
	public class SaveCatalogSkuCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void{
			var scse:SaveCatalogSkuEvent = SaveCatalogSkuEvent(event);
			var sku:Sku = scse.sku;
			var delegate:BroadleafCommerceAdminServiceDelegate = new BroadleafCommerceAdminServiceDelegate(this);
			delegate.saveSku(sku);				
		}
		
		public function result(data:Object):void{
			var facse:FindAllCatalogSkusEvent = new FindAllCatalogSkusEvent();
			facse.dispatch();
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}
	}
}