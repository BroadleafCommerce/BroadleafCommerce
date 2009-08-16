package org.broadleafcommerce.admin.core.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class InitializeApplicationEvent extends CairngormEvent
	{
		public static const EVENT_INITIALIZE_APPLICATION:String = "event_initialize_Application";
		
		public function InitializeApplicationEvent()
		{
			super(EVENT_INITIALIZE_APPLICATION);
		}
		
	}
}