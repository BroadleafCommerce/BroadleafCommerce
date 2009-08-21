package org.broadleafcommerce.admin.catalog.control.events.media
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.media.Media;

	public class EditMediaEvent extends CairngormEvent
	{
		public static const EVENT_EDIT_MEDIA_EVENT:String = "edit_media_event";
		
		public var media:Media;
		
		public function EditMediaEvent(media:Media)
		{
			super(EVENT_EDIT_MEDIA_EVENT);
			this.media = media;
		}
		
	}
}