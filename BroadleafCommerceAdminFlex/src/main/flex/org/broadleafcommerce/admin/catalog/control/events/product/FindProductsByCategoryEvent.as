package org.broadleafcommerce.admin.catalog.control.events.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class FindProductsByCategoryEvent extends CairngormEvent
	{
		public static const EVENT_FIND_PRODUCTS_BY_CATEGORY:String = "find_products_by_category_event";
		
		public var category:Category;
		
		public function FindProductsByCategoryEvent(category:Category)
		{
			super(EVENT_FIND_PRODUCTS_BY_CATEGORY);
			this.category = category;
		}
		
	}
}