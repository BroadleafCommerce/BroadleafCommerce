package org.broadleafcommerce.admin.catalog.control.events.sku
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;
	
	public class SaveSkuEvent extends CairngormEvent
	{
		
		public static const EVENT_SAVE_CATALOG_SKU:String = "event_save_catalog_sku";
		
		public var sku:Sku;
		
		public function SaveSkuEvent(sku:Sku)
		{
			super(EVENT_SAVE_CATALOG_SKU);
			this.sku = sku;
		}

	}
}