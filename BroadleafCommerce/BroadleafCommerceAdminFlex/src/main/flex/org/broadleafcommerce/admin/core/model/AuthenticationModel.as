package org.broadleafcommerce.admin.core.model
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.core.vo.security.AdminUser;
	
	[Bindable]
	public class AuthenticationModel
	{
		public static const STATE_APP_AUTHENTICATED:String = "app_authenticated_state";
		public static const STATE_APP_ANONYMOUS:String = "app_anonymous_state";
		
		public var authenticatedState:String = STATE_APP_ANONYMOUS;
		
		public var username:String = "";
		
		public var resultString:String = "";
		
		public var userPrincipal:AdminUser = new AdminUser();
		
		public var authenticatedModules:ArrayCollection = new ArrayCollection();

	}
}