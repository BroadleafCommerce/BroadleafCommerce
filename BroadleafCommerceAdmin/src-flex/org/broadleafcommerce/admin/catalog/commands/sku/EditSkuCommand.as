package org.broadleafcommerce.admin.catalog.commands.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.sku.EditSkuEvent;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.model.SkuModel;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;

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