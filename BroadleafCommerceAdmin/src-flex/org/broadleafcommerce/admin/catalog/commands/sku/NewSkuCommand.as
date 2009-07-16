package org.broadleafcommerce.admin.catalog.commands.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.sku.NewSkuEvent;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;

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