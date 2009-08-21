package org.broadleafcommerce.admin.catalog.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.Media;

	public class SaveMediaEvent extends CairngormEvent
	{
		public static const EVENT_SAVE_MEDIA:String = "save_media_event";

		public var media:Media;
		
		public function SaveMediaEvent(media:Media)
		{
			super(EVENT_SAVE_MEDIA);
			this.media = media;
		}
		
	}
}