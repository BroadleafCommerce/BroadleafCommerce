package org.broadleafcommerce.admin.security.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;

	import org.broadleafcommerce.admin.core.vo.security.AdminPermission;
	import org.broadleafcommerce.admin.core.vo.security.AdminRole;
	import org.broadleafcommerce.admin.core.vo.security.AdminUser;

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

		public function findAdminUserByUsername(username:String):void{
			var call:AsyncToken = adminSecurityService.readAdminUserByUserName(username);
			call.addResponder(responder);
		}

		public function deleteAdminPermission(permission:AdminPermission):void{
			var call:AsyncToken = adminSecurityService.deleteAdminPermission(permission);
			call.addResponder(responder);
		}

		public function deleteAdminRole(role:AdminRole):void{
			var call:AsyncToken = adminSecurityService.deleteAdminRole(role);
			call.addResponder(responder);
		}

		public function deleteAdminUser(adminUser:AdminUser):void{
			var call:AsyncToken = adminSecurityService.deleteAdminUser(adminUser);
			call.addResponder(responder);
		}

		public function saveAdminPermission(permission:AdminPermission):void{
			var call:AsyncToken = adminSecurityService.saveAdminPermission(permission);
			call.addResponder(responder);
		}

		public function saveRole(role:AdminRole):void{
			var call:AsyncToken = adminSecurityService.saveAdminRole(role);
			call.addResponder(responder);
		}

		public function saveAdminUser(adminUser:AdminUser):void{
			var call:AsyncToken = adminSecurityService.saveAdminUser(adminUser);
			call.addResponder(responder);
		}
	}
}