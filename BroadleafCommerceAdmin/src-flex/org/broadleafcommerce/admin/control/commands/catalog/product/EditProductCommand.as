package org.broadleafcommerce.admin.control.commands.catalog.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	
	import org.broadleafcommerce.admin.control.events.catalog.product.EditProductEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.ViewCurrentProductEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.view.ProductModel;
	import org.broadleafcommerce.admin.model.view.SkuModel;
	
	public class EditProductCommand implements Command
	{
		
		public function execute(event:CairngormEvent):void{
			var ecpc:EditProductEvent = EditProductEvent(event);
			var productModel:ProductModel = AppModelLocator.getInstance().productModel;
			var skuModel:SkuModel = AppModelLocator.getInstance().skuModel;
			
			
			if(AppModelLocator.getInstance().productModel.currentProductChanged){
				// replace this with a real pop-up
				Alert.show("Save current Changes to product?");
				AppModelLocator.getInstance().productModel.currentProductChanged = false;
			}
			
			productModel.currentProduct = ecpc.product;
			skuModel.viewSkus = ecpc.product.allSkus;
			productModel.viewState = ProductModel.STATE_VIEW_EDIT;
			if(ecpc.switchView){
				var vcpe:ViewCurrentProductEvent = new ViewCurrentProductEvent();
				vcpe.dispatch();
			}
		}

	}
}