package org.broadleafcommerce.admin.control.events.catalog.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;
	
	public class SaveCatalogCategoryEvent extends CairngormEvent{
		public static const EVENT_SAVE_CATALOG_CATEGORY:String = "event_save_catalog_category";
		public var category:Category;
		
		public function SaveCatalogCategoryEvent(category:Category){
			super(EVENT_SAVE_CATALOG_CATEGORY);
			this.category = category;
		}
	}
}