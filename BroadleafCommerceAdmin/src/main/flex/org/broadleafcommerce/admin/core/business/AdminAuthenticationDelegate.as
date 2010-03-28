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
package org.broadleafcommerce.admin.core.business
{
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.remoting.RemoteObject;
	
	public class AdminAuthenticationDelegate
	{
		
		private var responder:IResponder;
		private var securityService:RemoteObject;		
		
		public function AdminAuthenticationDelegate(responder:IResponder)
		{
			this.securityService = ServiceLocator.getInstance().getRemoteObject("blcAdminSecurityService");
			this.responder = responder;
		}

		public function authenticateUser(username:String, password:String):void{
			securityService.logout();
			securityService.setCredentials(username, password);
			var call:AsyncToken = securityService.readAdminUserByUserName(username);
			call.addResponder(responder);
		}
		
		public function logout():void{
			securityService.logout();
		}

		public function readAdminUserById(id:int):void{
			var call:AsyncToken = securityService.readAdminUserById(id);
			call.addResponder(responder);
		}
		

	}
}