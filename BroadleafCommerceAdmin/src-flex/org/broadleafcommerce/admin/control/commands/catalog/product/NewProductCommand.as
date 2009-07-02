package org.broadleafcommerce.admin.control.commands.catalog.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;
	import org.broadleafcommerce.admin.model.view.ProductModel;

	public class NewProductCommand implements Command
	{
		
		public function execute(event:CairngormEvent):void
		{
			AppModelLocator.getInstance().productModel.currentProduct = new Product();
			AppModelLocator.getInstance().productModel.viewState = ProductModel.STATE_NEW;
		}
		
	}
}