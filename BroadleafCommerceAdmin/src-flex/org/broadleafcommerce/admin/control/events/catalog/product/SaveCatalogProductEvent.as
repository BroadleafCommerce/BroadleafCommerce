package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;
	
	public class SaveCatalogProductEvent extends CairngormEvent
	{
		public static const EVENT_SAVE_CATALOG_PRODUCT:String = "event_save_catalog_product";
		public var product:Product;
		
		public function SaveCatalogProductEvent(product:Product)
		{
			super(EVENT_SAVE_CATALOG_PRODUCT);
			this.product = product;
		}
	}
}