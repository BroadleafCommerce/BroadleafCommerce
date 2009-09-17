package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	
	public class SaveCategoryEvent extends CairngormEvent{
		public static const EVENT_SAVE_CATALOG_CATEGORY:String = "event_save_catalog_category";
		public var category:Category;
		public var nextEvent:CairngormEvent;		
		public function SaveCategoryEvent(category:Category, nextEvent:CairngormEvent=null){
			super(EVENT_SAVE_CATALOG_CATEGORY);
			this.category = category;
			this.nextEvent = nextEvent;
		}
	}
}