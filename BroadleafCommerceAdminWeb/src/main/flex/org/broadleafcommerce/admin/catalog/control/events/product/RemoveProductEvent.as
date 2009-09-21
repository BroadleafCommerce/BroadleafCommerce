package org.broadleafcommerce.admin.catalog.control.events.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class RemoveProductEvent extends CairngormEvent
	{
		public static const EVENT_REMOVE_PRODUCT:String = "remove_product_event";

		public var product:Product;
		
		public function RemoveProductEvent(product:Product)
		{
			super(EVENT_REMOVE_PRODUCT);
			this.product = product;
		}
		
	}
}