package org.broadleafcommerce.admin.security.commands.permissions
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;

	import org.broadleafcommerce.admin.security.business.SecurityServiceDelegate;
	import org.broadleafcommerce.admin.security.control.events.FindAllPermissionsEvent;
	import org.broadleafcommerce.admin.security.control.events.SaveAdminPermissionEvent;
	import org.broadleafcommerce.admin.core.vo.security.AdminPermission;


	public class SaveAdminPermissionCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var se:SaveAdminPermissionEvent = event as SaveAdminPermissionEvent;
			var delegate:SecurityServiceDelegate = new SecurityServiceDelegate(this);
			delegate.saveAdminPermission(se.permission);
		}

		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			var permission:AdminPermission = event.result as AdminPermission;
			var reloadPermissions:FindAllPermissionsEvent = new FindAllPermissionsEvent();
			reloadPermissions.dispatch();
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}