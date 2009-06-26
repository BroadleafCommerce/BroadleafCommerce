package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class NewCatalogProductEvent extends CairngormEvent
	{
		public static const EVENT_NEW_CATALOG_PRODUCT:String = "event_new_catalog_product";
		
		public function NewCatalogProductEvent()
		{
			super(EVENT_NEW_CATALOG_PRODUCT);
		}
		
	}
}