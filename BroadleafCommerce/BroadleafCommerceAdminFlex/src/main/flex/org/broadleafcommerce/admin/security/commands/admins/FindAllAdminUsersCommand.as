package org.broadleafcommerce.admin.security.commands.admins
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;

	import org.broadleafcommerce.admin.security.business.BroadleafCommerceAdminSecurityServiceDelegate;
	import org.broadleafcommerce.admin.security.model.SecurityModelLocator;

	public class FindAllAdminUsersCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var delegate:BroadleafCommerceAdminSecurityServiceDelegate = new BroadleafCommerceAdminSecurityServiceDelegate(this);
			delegate.findAllAdminUsers();
		}

		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			var users:ArrayCollection = event.result as ArrayCollection;
			SecurityModelLocator.getInstance().adminUsers = users;
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}