package org.broadleafcommerce.admin.control.events.catalog.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;
	
	public class EditCategoryEvent extends CairngormEvent
	{
		
		public static const EVENT_EDIT_CATALOG_CATEGORY:String = "event_edit_catalog_category";
		
		public var category:Category;
		
		public function EditCategoryEvent(category:Category)
		{
			super(EVENT_EDIT_CATALOG_CATEGORY);
			this.category = category;
		}

	}
}