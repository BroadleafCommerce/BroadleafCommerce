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
package org.broadleafcommerce.admin.security.model
{
	[Bindable]
	public class SecurityModel
	{
		public function SecurityModel()
		{
		}
		public static const SERVICE_ID:String = "blAdminSecurityService";

		public static const STATE_VIEW_ADMINS:String = "view_admins_state";
		public static const STATE_VIEW_ROLES:String = "view_roles_state";
		public static const STATE_VIEW_PERMISSIONS:String = "view_permissions_state";

		public var viewState:String = STATE_VIEW_ADMINS;
	}
}