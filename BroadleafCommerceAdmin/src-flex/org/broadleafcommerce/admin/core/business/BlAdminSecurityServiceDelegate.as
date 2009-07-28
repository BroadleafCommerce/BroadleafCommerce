package org.broadleafcommerce.admin.core.business
{
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.remoting.RemoteObject;
	
	public class BlAdminSecurityServiceDelegate
	{
		
		private var responder:IResponder;
		private var securityService:RemoteObject;		
		
		public function BlAdminSecurityServiceDelegate(responder:IResponder)
		{
			this.securityService = ServiceLocator.getInstance().getRemoteObject("blcAdminSecurityService");
			this.responder = responder;
		}
		
		public function authenticateUser(username:String, password:String):void{
			securityService.setCredentials(username, password);
			var call:AsyncToken = securityService.readAdminUserByUserName(username);
			call.addResponder(responder);
		}

		public function readAdminUserById(id:int):void{
			var call:AsyncToken = securityService.readAdminUserById(id);
			call.addResponder(responder);
		}
		

	}
}