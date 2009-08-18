package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class FindAllCategoriesEvent extends CairngormEvent
	{
		public static const EVENT_FIND_ALL_CATALOG_CATEGORIES:String = "event_find_all_catalog_categories";
		
		public function FindAllCategoriesEvent()
		{
			super(EVENT_FIND_ALL_CATALOG_CATEGORIES);
		}
		
	}
}