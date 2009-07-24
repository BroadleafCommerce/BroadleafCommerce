package org.broadleafcommerce.admin.security.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;

	public class BroadleafCommerceAdminSecurityServiceDelegate
	{
        private var responder : IResponder;
        private var adminSecurityService:Object;

		public function BroadleafCommerceAdminSecurityServiceDelegate(responder:IResponder)
		{
			this.adminSecurityService = AdminSecurityServiceLocator.getInstance().getService();
			this.responder = responder;
		}

 		public function findAllAdminUsers():void{
			var call:AsyncToken = adminSecurityService.readAllAdminUsers();
			call.addResponder(responder);
		}

 		public function findAllAdminRoles():void{
			var call:AsyncToken = adminSecurityService.readAllAdminRoles();
			call.addResponder(responder);
		}

 		public function findAllAdminPermissions():void{
			var call:AsyncToken = adminSecurityService.readAllAdminPermissions();
			call.addResponder(responder);
		}

		public function findAdminUserById(id:int):void{
			var call:AsyncToken = adminSecurityService.readAdminUserById(id);
			call.addResponder(responder);
		}

	}
}