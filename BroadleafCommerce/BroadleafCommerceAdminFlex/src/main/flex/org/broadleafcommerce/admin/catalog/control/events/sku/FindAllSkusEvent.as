package org.broadleafcommerce.admin.catalog.control.events.sku
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class FindAllSkusEvent extends CairngormEvent
	{
		public static const EVENT_FIND_ALL_CATALOG_SKUS:String = "find_all_catalog_skus";
		
		public function FindAllSkusEvent()
		{
			super(EVENT_FIND_ALL_CATALOG_SKUS);
		}

	}
}