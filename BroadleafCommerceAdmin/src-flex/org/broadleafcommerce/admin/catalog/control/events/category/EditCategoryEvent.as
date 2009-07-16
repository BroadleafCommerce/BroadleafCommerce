package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	
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