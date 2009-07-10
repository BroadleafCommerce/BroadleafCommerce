package org.broadleafcommerce.admin.control.commands.catalog.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.product.FindAllProductsEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.SaveProductEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.SaveSkuEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.business.BroadleafCommerceAdminServiceDelegate;
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;
	import org.broadleafcommerce.admin.model.data.remote.catalog.sku.Sku;
	
	public class SaveProductCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void{
			var scpe:SaveProductEvent = SaveProductEvent(event);
			var product:Product = scpe.product;
			var delegate:BroadleafCommerceAdminServiceDelegate = new BroadleafCommerceAdminServiceDelegate(this);			
			delegate.saveProduct(product);			
//			var sse:SaveSkuEvent = new SaveSkuEvent(Sku(product.allSkus.getItemAt(0)));
//			sse.dispatch();			
		}
		
		public function result(data:Object):void{
			AppModelLocator.getInstance().productModel.currentProductChanged = false;
			var facpe:FindAllProductsEvent = new FindAllProductsEvent();
			facpe.dispatch();
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}
	}
}