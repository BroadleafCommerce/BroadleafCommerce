package org.broadleafcommerce.admin.tools.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class CreateCodeTypeEvent extends CairngormEvent
	{
		
		public static const EVENT_CREATE_CODE_TYPE:String = "event_create_code_type";
		
		public function CreateCodeTypeEvent()
		{
			super(EVENT_CREATE_CODE_TYPE);
		}

	}
}