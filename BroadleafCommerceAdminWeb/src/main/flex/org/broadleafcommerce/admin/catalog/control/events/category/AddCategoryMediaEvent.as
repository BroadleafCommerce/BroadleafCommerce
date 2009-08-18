package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class AddCategoryMediaEvent extends CairngormEvent
	{
		public static const EVENT_ADD_CATEGORY_MEDIA:String = "add_category_media_event";
		
		public function AddCategoryMediaEvent()
		{
			super(EVENT_ADD_CATEGORY_MEDIA);
		}
		
	}
}