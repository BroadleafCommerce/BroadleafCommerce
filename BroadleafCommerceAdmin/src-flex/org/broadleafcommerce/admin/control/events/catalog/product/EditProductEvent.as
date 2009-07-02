package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;

	public class EditProductEvent extends CairngormEvent
	{
		public static const EVENT_EDIT_CATALOG_PRODUCT:String = "event_edit_catalog_product";

		public var product:Product;
		
		public function EditProductEvent(product:Product)
		{
			super(EVENT_EDIT_CATALOG_PRODUCT);
			this.product = product;
		}
	}
}