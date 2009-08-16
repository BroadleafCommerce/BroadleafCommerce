package org.broadleafcommerce.admin.security.commands.roles
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;

	import org.broadleafcommerce.admin.security.model.SecurityModel;
	import org.broadleafcommerce.admin.security.model.SecurityModelLocator;

	public class ViewRolesCommand implements Command
	{
		public function ViewRolesCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var securityModel:SecurityModel = SecurityModelLocator.getInstance().securityModel;
			securityModel.viewState = SecurityModel.STATE_VIEW_ROLES;
		}

	}
}