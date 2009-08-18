package org.broadleafcommerce.admin.security.commands.admins
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;

	import org.broadleafcommerce.admin.security.business.BroadleafCommerceAdminSecurityServiceDelegate;
	import org.broadleafcommerce.admin.security.control.events.DeleteAdminUserEvent;
	import org.broadleafcommerce.admin.security.control.events.FindAllAdminUsersEvent;


	public class DeleteAdminUserCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var de:DeleteAdminUserEvent = event as DeleteAdminUserEvent;
			var delegate:BroadleafCommerceAdminSecurityServiceDelegate = new BroadleafCommerceAdminSecurityServiceDelegate(this);
			delegate.deleteAdminUser(de.adminUser);
		}

		public function result(data:Object):void
		{
			var reloadAdmins:FindAllAdminUsersEvent = new FindAllAdminUsersEvent();
			reloadAdmins.dispatch();
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}