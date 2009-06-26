package org.broadleafcommerce.admin.control.commands.catalog.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.view.ProductModel;
	
	public class EditCatalogProductCommand implements Command
	{
		
		public function execute(event:CairngormEvent):void{
			AppModelLocator.getInstance().productModel.currentProduct = event.product;
			AppModelLocator.getInstance().productModel.viewState = ProductModel.STATE_EDIT;
		}

	}
}