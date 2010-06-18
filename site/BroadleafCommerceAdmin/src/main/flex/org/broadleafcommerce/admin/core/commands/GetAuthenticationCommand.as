/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.core.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.universalmind.cairngorm.commands.Command;
	
	import mx.controls.Alert;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.core.business.AdminAuthenticationDelegate;
	import org.broadleafcommerce.admin.core.control.events.GetAuthenticationEvent;
	import org.broadleafcommerce.admin.core.control.events.LoadModulesEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.model.AuthenticationModel;
	import org.broadleafcommerce.admin.core.model.ConfigModel;
	import org.broadleafcommerce.admin.core.vo.security.AdminUser;

	public class GetAuthenticationCommand extends Command 
	// implements Command, IResponder
	{
		public function GetAuthenticationCommand()
		{
		}

		override public function execute(event:CairngormEvent):void
		{
			var gae:GetAuthenticationEvent = GetAuthenticationEvent(event);
			var username:String = gae.username;
			var password:String = gae.password;
			var securityDelegate:AdminAuthenticationDelegate = new AdminAuthenticationDelegate(this);
			securityDelegate.authenticateUser(username, password);
			
		}
		
		override public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);			
			var authModel:AuthenticationModel = AppModelLocator.getInstance().authModel;
			var configModel:ConfigModel = AppModelLocator.getInstance().configModel;
			authModel.userPrincipal = AdminUser(event.result);
			authModel.authenticatedState = AuthenticationModel.STATE_APP_AUTHENTICATED;
			var lme:LoadModulesEvent = new LoadModulesEvent(configModel.moduleConfigs);
			lme.dispatch();	
		}
		
		override public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			if(event.fault.faultCode == "Channel.Authentication.Error"){
				AppModelLocator.getInstance().authModel.resultString = event.fault.faultDetail;
			}else{
				Alert.show("Error: "+ event);				
			}
			var securityDelegate:AdminAuthenticationDelegate = new AdminAuthenticationDelegate(this);
			securityDelegate.logout();
		}		
		
	}
}