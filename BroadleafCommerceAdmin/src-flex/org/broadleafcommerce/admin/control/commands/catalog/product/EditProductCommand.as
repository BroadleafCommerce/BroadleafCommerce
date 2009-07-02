package org.broadleafcommerce.admin.control.commands.catalog.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.product.EditProductEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.view.CategoryModel;
	import org.broadleafcommerce.admin.model.view.ProductModel;
	
	public class EditProductCommand implements Command
	{
		
		public function execute(event:CairngormEvent):void{
			var ecpc:EditProductEvent = EditProductEvent(event);
			AppModelLocator.getInstance().productModel.currentProduct = ecpc.product;
			AppModelLocator.getInstance().productModel.viewState = ProductModel.STATE_EDIT;
		}

	}
}