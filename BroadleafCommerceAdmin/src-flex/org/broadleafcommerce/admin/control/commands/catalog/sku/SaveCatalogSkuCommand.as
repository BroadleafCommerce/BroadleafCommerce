package org.broadleafcommerce.admin.control.commands.catalog.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.sku.FindAllSkusEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.SaveSkuEvent;
	import org.broadleafcommerce.admin.model.business.BroadleafCommerceAdminServiceDelegate;
	import org.broadleafcommerce.admin.model.data.remote.catalog.sku.Sku;
	
	public class SaveCatalogSkuCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void{
			var scse:SaveSkuEvent = SaveSkuEvent(event);
			var sku:Sku = scse.sku;
			var delegate:BroadleafCommerceAdminServiceDelegate = new BroadleafCommerceAdminServiceDelegate(this);
			delegate.saveSku(sku);				
		}
		
		public function result(data:Object):void{
			var facse:FindAllSkusEvent = new FindAllSkusEvent();
			facse.dispatch();
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}
	}
}