package org.broadleafcommerce.admin.control.events.catalog.sku
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;

	public class NewSkuEvent extends CairngormEvent
	{
		public static const EVENT_NEW_SKU:String = "new_sku_event";
		
		public var product:Product;
		
		public function NewSkuEvent(product:Product)
		{
			super(EVENT_NEW_SKU);
			this.product = product;
		}
		
	}
}