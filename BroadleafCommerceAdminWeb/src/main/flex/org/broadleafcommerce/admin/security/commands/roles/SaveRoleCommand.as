package org.broadleafcommerce.admin.security.commands.roles
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;

	import org.broadleafcommerce.admin.security.business.BroadleafCommerceAdminSecurityServiceDelegate;
	import org.broadleafcommerce.admin.security.control.events.FindAllRolesEvent;
	import org.broadleafcommerce.admin.security.control.events.SaveRoleEvent;

	public class SaveRoleCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var sre:SaveRoleEvent = event as SaveRoleEvent;
			var delegate:BroadleafCommerceAdminSecurityServiceDelegate = new BroadleafCommerceAdminSecurityServiceDelegate(this);
			delegate.saveRole(sre.role);
		}

		public function result(data:Object):void
		{
			var findRoles:FindAllRolesEvent = new FindAllRolesEvent();
			findRoles.dispatch();
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}