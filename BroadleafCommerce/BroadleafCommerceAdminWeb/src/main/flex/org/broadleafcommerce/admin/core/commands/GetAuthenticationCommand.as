package org.broadleafcommerce.admin.core.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.core.business.BlAdminSecurityServiceDelegate;
	import org.broadleafcommerce.admin.core.control.events.GetAuthenticationEvent;
	import org.broadleafcommerce.admin.core.control.events.LoadModulesEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.model.AuthenticationModel;
	import org.broadleafcommerce.admin.core.vo.security.AdminUser;

	public class GetAuthenticationCommand implements Command, IResponder
	{
		public function GetAuthenticationCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var gae:GetAuthenticationEvent = GetAuthenticationEvent(event);
			var username:String = gae.username;
			var password:String = gae.password;
			var securityDelegate:BlAdminSecurityServiceDelegate = new BlAdminSecurityServiceDelegate(this);
			securityDelegate.authenticateUser(username, password);
			
		}
		
		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);			
			var authModel:AuthenticationModel = AppModelLocator.getInstance().authModel;
			authModel.userPrincipal = AdminUser(event.result);
			authModel.authenticatedState = AuthenticationModel.STATE_APP_AUTHENTICATED;
			var lme:LoadModulesEvent = new LoadModulesEvent(AppModelLocator.getInstance().configModel.modules);
			lme.dispatch();	
			
		}
		
		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			if(event.fault.faultCode == "Channel.Authentication.Error"){
				AppModelLocator.getInstance().authModel.resultString = event.fault.faultDetail;
			}else{
				Alert.show("Error: "+ event);				
			}
			var securityDelegate:BlAdminSecurityServiceDelegate = new BlAdminSecurityServiceDelegate(this);
			securityDelegate.logout();
			
		}		
		
	}
}