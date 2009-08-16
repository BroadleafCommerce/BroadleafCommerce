package org.broadleafcommerce.admin.security.commands.roles
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;

	import org.broadleafcommerce.admin.security.business.BroadleafCommerceAdminSecurityServiceDelegate;
	import org.broadleafcommerce.admin.security.control.events.DeleteAdminRoleEvent;
	import org.broadleafcommerce.admin.security.control.events.FindAllRolesEvent;


	public class DeleteAdminRoleCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var de:DeleteAdminRoleEvent = event as DeleteAdminRoleEvent;
			var delegate:BroadleafCommerceAdminSecurityServiceDelegate = new BroadleafCommerceAdminSecurityServiceDelegate(this);
			delegate.deleteAdminRole(de.role);
		}

		public function result(data:Object):void
		{
			var reloadRoles:FindAllRolesEvent = new FindAllRolesEvent();
			reloadRoles.dispatch();
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}