package org.broadleafcommerce.admin.core.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetAuthenticationEvent extends CairngormEvent
	{
		public static const EVENT_GET_AUTHENTICATION:String = "get_authentication_event";
		
		public var username:String;
		public var password:String;
		
		public function GetAuthenticationEvent(username:String, password:String)
		{
			super(EVENT_GET_AUTHENTICATION);
			this.username = username;
			this.password = password;
		}
		
	}
}