package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.BroadleafCommerceAdminCatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.product.FindProductsByCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	
	public class SaveProductCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void{
			var scpe:SaveProductEvent = SaveProductEvent(event);
			var product:Product = scpe.product;
			var delegate:BroadleafCommerceAdminCatalogServiceDelegate = new BroadleafCommerceAdminCatalogServiceDelegate(this);			
			delegate.saveProduct(product);			
//			var sse:SaveSkuEvent = new SaveSkuEvent(Sku(product.allSkus.getItemAt(0)));
//			sse.dispatch();			
		}
		
		public function result(data:Object):void{
			CatalogModelLocator.getInstance().productModel.currentProductChanged = false;
//			var facpe:FindAllProductsEvent = new FindAllProductsEvent();
//			facpe.dispatch();
			var fpbce:FindProductsByCategoryEvent = new FindProductsByCategoryEvent(CatalogModelLocator.getInstance().categoryModel.currentCategory);
			fpbce.dispatch();
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}
	}
}