package org.broadleafcommerce.admin.catalog.control.events.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class EditProductEvent extends CairngormEvent
	{
		public static const EVENT_EDIT_CATALOG_PRODUCT:String = "event_edit_catalog_product";

		public var product:Product;
		public var switchView:Boolean;
		
		public function EditProductEvent(product:Product, switchToProductView:Boolean)
		{
			super(EVENT_EDIT_CATALOG_PRODUCT);
			this.product = product;
			this.switchView = switchToProductView;
		}
	}
}