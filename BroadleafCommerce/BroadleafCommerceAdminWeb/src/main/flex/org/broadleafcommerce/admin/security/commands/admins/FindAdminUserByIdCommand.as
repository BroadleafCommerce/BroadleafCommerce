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
	import org.broadleafcommerce.admin.security.control.events.FindAdminUserByIdEvent;
	import org.broadleafcommerce.admin.security.model.SecurityModelLocator;

	public class FindAdminUserByIdCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var fau:FindAdminUserByIdEvent = event as FindAdminUserByIdEvent;
			var delegate:BroadleafCommerceAdminSecurityServiceDelegate = new BroadleafCommerceAdminSecurityServiceDelegate(this);
			delegate.findAdminUserById(fau.id);
		}

		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			//var users:ArrayCollection = new ArrayCollection();
			//var adminUser:AdminUser = new AdminUser();
			//adminUser = event.result as AdminUser;
			//users.addItem(adminUser);
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}