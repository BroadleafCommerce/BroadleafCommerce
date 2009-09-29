package org.broadleafcommerce.admin.catalog.control.events.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class AddCrossSaleProductEvent extends CairngormEvent
	{
		public static const EVENT_ADD_CROSSALE_PRODUCT:String = "add_crosssale_product_event";
		
		public var crossProduct:Product;
		public var product:Product;
		public var index:int;
		
		public function AddCrossSaleProductEvent(crossProduct:Product, product:Product, index:int = -1)
		{
			super(EVENT_ADD_CROSSALE_PRODUCT);
			this.crossProduct = crossProduct;
			this.product = product;
			this.index = index;
		}
		
	}
}