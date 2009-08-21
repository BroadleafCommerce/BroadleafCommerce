package org.broadleafcommerce.admin.catalog.control.events.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.Media;

	public class EditCategoryMediaEvent extends CairngormEvent
	{
		public static const EVENT_EDIT_CATEGORY_MEDIA_EVENT:String = "edit_category_media_event";
		
		public var media:Media;
		
		public function EditCategoryMediaEvent(media:Media)
		{
			super(EVENT_EDIT_CATEGORY_MEDIA_EVENT);
			this.media = media;
		}
		
	}
}