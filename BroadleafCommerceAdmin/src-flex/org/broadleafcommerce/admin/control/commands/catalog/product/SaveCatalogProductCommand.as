package org.broadleafcommerce.admin.control.commands.catalog.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.product.FindAllCatalogProductsEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.SaveCatalogProductEvent;
	import org.broadleafcommerce.admin.model.business.BroadleafCommerceAdminServiceDelegate;
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;
	
	public class SaveCatalogProductCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void{
			var scpe:SaveCatalogProductEvent = SaveCatalogProductEvent(event);
			var product:Product = scpe.product;
			var delegate:BroadleafCommerceAdminServiceDelegate = new BroadleafCommerceAdminServiceDelegate(this);
			delegate.saveProduct(product);			
		}
		
		public function result(data:Object):void{
			var facpe:FindAllCatalogProductsEvent = new FindAllCatalogProductsEvent();
			facpe.dispatch();
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}
	}
}