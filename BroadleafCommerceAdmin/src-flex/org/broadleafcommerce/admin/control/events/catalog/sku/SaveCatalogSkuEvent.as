package org.broadleafcommerce.admin.control.events.catalog.sku
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.sku.Sku;
	
	public class SaveCatalogSkuEvent extends CairngormEvent
	{
		
		public static const EVENT_SAVE_CATALOG_SKU:String = "event_save_catalog_sku";
		
		public var sku:Sku;
		
		public function SaveCatalogSkuEvent(sku:Sku)
		{
			super(EVENT_SAVE_CATALOG_SKU);
			this.sku = sku;
		}

	}
}