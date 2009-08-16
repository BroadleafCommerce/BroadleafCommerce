package org.broadleafcommerce.admin.catalog.commands.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.sku.NewSkuEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;

	public class NewSkuCommand implements Command
	{
		public function NewSkuCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var nse:NewSkuEvent = NewSkuEvent(event);
			CatalogModelLocator.getInstance().skuModel.currentSku = new Sku();
			CatalogModelLocator.getInstance().skuModel.currentSku.allParentProducts.addItem(nse.product);
		}
		
	}
}