package org.broadleafcommerce.admin.core.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetAdminConfigEvent extends CairngormEvent
	{
		public static const EVENT_READ_ADMIN_CONFIG:String = "read_admin_config_event";
		
		public function GetAdminConfigEvent()
		{
			super(EVENT_READ_ADMIN_CONFIG);			
		}
		
	}
}