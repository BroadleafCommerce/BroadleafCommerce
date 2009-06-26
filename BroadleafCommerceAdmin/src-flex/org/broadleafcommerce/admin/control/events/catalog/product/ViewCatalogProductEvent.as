package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class ViewCatalogProductEvent extends CairngormEvent
	{
		public static const EVENT_VIEW_CATALOG_PRODUCT:String = "event_view_catalog_product";
		
		public function ViewCatalogProductEvent()
		{
			super(EVENT_VIEW_CATALOG_PRODUCT);
		}

	}
}