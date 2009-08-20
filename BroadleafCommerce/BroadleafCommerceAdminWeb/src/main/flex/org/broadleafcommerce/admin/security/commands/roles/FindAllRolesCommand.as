package org.broadleafcommerce.admin.security.commands.roles
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;

	import org.broadleafcommerce.admin.security.business.SecurityServiceDelegate;
	import org.broadleafcommerce.admin.security.model.SecurityModelLocator;

	public class FindAllRolesCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var delegate:SecurityServiceDelegate = new SecurityServiceDelegate(this);
			delegate.findAllAdminRoles();
		}

		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			var roles:ArrayCollection = event.result as ArrayCollection;
			SecurityModelLocator.getInstance().adminRoles = roles;
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}