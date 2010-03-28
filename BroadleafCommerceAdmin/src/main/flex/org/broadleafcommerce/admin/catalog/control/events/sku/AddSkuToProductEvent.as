package org.broadleafcommerce.admin.catalog.control.events.sku
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;
	
	public class AddSkuToProductEvent extends CairngormEvent
	{
		
		public static const EVENT_ADD_SKU_TO_PRODUCT:String = "event_add_sku_to_product";
		
		public var sku:Sku;
		
		public function AddSkuToProductEvent(sku:Sku)
		{
			super(EVENT_ADD_SKU_TO_PRODUCT);
			this.sku = sku;
		}

	}
}