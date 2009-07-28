package org.broadleafcommerce.admin.security.commands.admins
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;

	import org.broadleafcommerce.admin.security.business.BroadleafCommerceAdminSecurityServiceDelegate;
	import org.broadleafcommerce.admin.security.control.events.FindAllAdminUsersEvent;
	import org.broadleafcommerce.admin.security.control.events.SaveAdminUserEvent;

	public class SaveAdminUserCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var sau:SaveAdminUserEvent = event as SaveAdminUserEvent;
			var delegate:BroadleafCommerceAdminSecurityServiceDelegate = new BroadleafCommerceAdminSecurityServiceDelegate(this);
			delegate.saveAdminUser(sau.adminUser);
		}

		public function result(data:Object):void
		{
			var findAdmins:FindAllAdminUsersEvent = new FindAllAdminUsersEvent();
			findAdmins.dispatch();
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}