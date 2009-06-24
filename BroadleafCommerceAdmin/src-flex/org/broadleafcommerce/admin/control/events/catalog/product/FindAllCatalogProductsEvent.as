package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class FindAllCatalogProductsEvent extends CairngormEvent
	{
		public static const EVENT_FIND_ALL_PRODUCTS:String = "event_find_all_products";
		
		public function FindAllCatalogProductsEvent()
		{
			super(EVENT_FIND_ALL_PRODUCTS);
		}

	}
}