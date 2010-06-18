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
package org.broadleafcommerce.admin.core.model
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.core.vo.security.AdminUser;
	
	[Bindable]
	public class AuthenticationModel
	{
		public static const STATE_APP_AUTHENTICATED:String = "app_authenticated_state";
		public static const STATE_APP_ANONYMOUS:String = "app_anonymous_state";
		
		public var authenticatedState:String = STATE_APP_ANONYMOUS;
		
		public var username:String = "";
		
		public var resultString:String = "";
		
		public var userPrincipal:AdminUser = new AdminUser();
		
	}
}