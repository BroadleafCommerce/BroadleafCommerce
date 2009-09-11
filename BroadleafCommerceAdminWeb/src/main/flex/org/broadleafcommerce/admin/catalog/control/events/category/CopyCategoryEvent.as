package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class CopyCategoryEvent extends CairngormEvent
	{
		public static const EVENT_COPY_CATEGORY:String = "copy_category_event";
		
		public var movedCategory:Category;
		public var newParent:Category;
		
		public function CopyCategoryEvent(movedCategory:Category, newParent:Category)
		{
			super(EVENT_COPY_CATEGORY);
			this.movedCategory = movedCategory;
			this.newParent = newParent;
		}
		
	}
}