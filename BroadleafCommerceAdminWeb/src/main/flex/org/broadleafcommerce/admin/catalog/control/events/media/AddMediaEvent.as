package org.broadleafcommerce.admin.catalog.control.events.media
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class AddMediaEvent extends CairngormEvent
	{
		public static const EVENT_ADD_MEDIA:String = "add_media_event";
		
		public function AddMediaEvent()
		{
			super(EVENT_ADD_MEDIA);
		}
		
	}
}