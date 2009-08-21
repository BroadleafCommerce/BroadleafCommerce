package org.broadleafcommerce.admin.catalog.control.events.media
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class ShowFileUploadEvent extends CairngormEvent
	{
		public static const EVENT_SHOW_FILE_UPLOAD:String = "show_file_upload_event";
		
		public var viewData:Object;
		public var viewType:String;
		
		public function ShowFileUploadEvent(viewType:String,data:Object)
		{
			super(EVENT_SHOW_FILE_UPLOAD);
			this.viewType = viewType;
			this.viewData = data;
		}
		
	}
}