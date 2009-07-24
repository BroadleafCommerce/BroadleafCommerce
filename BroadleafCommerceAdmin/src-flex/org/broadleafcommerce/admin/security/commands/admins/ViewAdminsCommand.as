package org.broadleafcommerce.admin.security.commands.admins
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;

	import org.broadleafcommerce.admin.security.model.SecurityModel;
	import org.broadleafcommerce.admin.security.model.SecurityModelLocator;

	public class ViewAdminsCommand implements Command
	{
		public function ViewAdminsCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var securityModel:SecurityModel = SecurityModelLocator.getInstance().securityModel;
			securityModel.viewState = SecurityModel.STATE_VIEW_ADMINS;
		}

	}
}