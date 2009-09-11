package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class MoveCategoryEvent extends CairngormEvent
	{
		public static const EVENT_MOVE_CATEGORY:String = "move_category_event";
		
		public var movedCategory:Category;
		public var oldParent:Category;
		public var newParent:Category;
		
		public function MoveCategoryEvent(movedCategory:Category, oldParent:Category, newParent:Category)
		{
			super(EVENT_MOVE_CATEGORY);
			this.movedCategory = movedCategory;
			this.oldParent = oldParent;
			this.newParent = newParent;
		}
		
	}
}