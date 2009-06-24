package org.broadleafcommerce.admin.control.events.catalog.sku
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class FindAllCatalogSkusEvent extends CairngormEvent
	{
		public static const EVENT_FIND_ALL_CATALOG_SKUS:String = "find_all_catalog_skus";
		
		public function FindAllCatalogSkusEvent()
		{
			super(EVENT_FIND_ALL_CATALOG_SKUS);
		}

	}
}