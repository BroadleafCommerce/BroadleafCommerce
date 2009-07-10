package org.broadleafcommerce.admin.control.commands.catalog.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.sku.EditSkuEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.view.ProductModel;
	import org.broadleafcommerce.admin.model.view.SkuModel;

	public class EditSkuCommand implements Command
	{
		public function EditSkuCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var ese:EditSkuEvent = EditSkuEvent(event);
			var skuModel:SkuModel = AppModelLocator.getInstance().skuModel;
			skuModel.currentSku = ese.sku;
			
			if(ese.showSkusView){
				var productModel:ProductModel = AppModelLocator.getInstance().productModel;
				productModel.viewState = ProductModel.STATE_VIEW_SKUS;
			}
		}
		
	}
}