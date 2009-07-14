package org.broadleafcommerce.admin.control.events.catalog.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;

	public class NewCategoryEvent extends CairngormEvent
	{
		public static const EVENT_NEW_CATALOG_CATEGORY:String = "event_new_catalog_category";
		
		public var parentCategory:Category;
		
		public function NewCategoryEvent(parentCategory:Category= null)
		{
			super(EVENT_NEW_CATALOG_CATEGORY);
			this.parentCategory = parentCategory;
		}
		
	}
}