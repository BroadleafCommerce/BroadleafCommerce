package org.broadleafcommerce.admin.catalog.control.events.sku
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

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