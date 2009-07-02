package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class NewProductEvent extends CairngormEvent
	{
		public static const EVENT_NEW_CATALOG_PRODUCT:String = "event_new_catalog_product";
		
		public function NewProductEvent()
		{
			super(EVENT_NEW_CATALOG_PRODUCT);
		}
		
	}
}