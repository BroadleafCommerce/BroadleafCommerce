package org.broadleafcommerce.admin.catalog.control.events.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	
	public class SaveProductEvent extends CairngormEvent
	{
		public static const EVENT_SAVE_CATALOG_PRODUCT:String = "event_save_catalog_product";
		public var product:Product;
		
		public function SaveProductEvent(product:Product)
		{
			super(EVENT_SAVE_CATALOG_PRODUCT);
			this.product = product;
		}
	}
}