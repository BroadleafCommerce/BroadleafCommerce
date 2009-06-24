package org.broadleafcommerce.admin.control.events.catalog.category
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class FindAllCatalogCategoriesEvent extends CairngormEvent
	{
		public static const EVENT_FIND_ALL_CATALOG_CATEGORIES:String = "event_find_all_catalog_categories";
		
		public function FindAllCatalogCategoriesEvent()
		{
			super(EVENT_FIND_ALL_CATALOG_CATEGORIES);
		}
		
	}
}