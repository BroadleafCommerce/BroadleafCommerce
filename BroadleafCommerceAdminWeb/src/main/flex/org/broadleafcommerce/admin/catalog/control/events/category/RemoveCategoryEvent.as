package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class RemoveCategoryEvent extends CairngormEvent
	{
		public static const EVENT_REMOVE_CATEGORY:String = "remove_category_event";
		
		public var category:Category;
		public var parentCategory:Category;
		
		public function RemoveCategoryEvent(category:Category, parentCategory:Category)
		{
			super(EVENT_REMOVE_CATEGORY);
			this.category = category;
			this.parentCategory = parentCategory;
		}
		
	}
}