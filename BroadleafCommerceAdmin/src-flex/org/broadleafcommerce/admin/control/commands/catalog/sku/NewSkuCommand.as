package org.broadleafcommerce.admin.control.commands.catalog.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.sku.NewSkuEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.remote.catalog.sku.Sku;

	public class NewSkuCommand implements Command
	{
		public function NewSkuCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var nse:NewSkuEvent = NewSkuEvent(event);
			AppModelLocator.getInstance().skuModel.currentSku = new Sku();
			AppModelLocator.getInstance().skuModel.currentSku.allParentProducts.addItem(nse.product);
		}
		
	}
}