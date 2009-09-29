package org.broadleafcommerce.admin.catalog.control.events.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.product.CrossSaleProduct;

	public class RemoveCrossSaleProductEvent extends CairngormEvent
	{
		public static const EVENT_REMOVE_CROSSSALE_PRODUCT:String = "remove_crosssale_product_event";
		
		public var product:Product;
		public var crossProduct:CrossSaleProduct;
		
		public function RemoveCrossSaleProductEvent(product:Product, crossProduct:CrossSaleProduct)
		{
			super(EVENT_REMOVE_CROSSSALE_PRODUCT);
			this.product = product;
			this.crossProduct = crossProduct;
		}
		
	}
}