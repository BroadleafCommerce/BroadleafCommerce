package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;

	public class EditCatalogProductEvent extends CairngormEvent
	{
		public static const EVENT_EDIT_CATALOG_PRODUCT:String = "event_edit_catalog_product";

		public var product:Product;
		
		public function EditCatalogProductEvent()
		{
			super(EVENT_EDIT_CATALOG_PRODUCT);
		}
	}
}