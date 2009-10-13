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
package org.broadleafcommerce.admin.core.control
{
	import com.adobe.cairngorm.control.FrontController;
	
	import org.broadleafcommerce.admin.core.commands.AddModulesToViewCommand;
	import org.broadleafcommerce.admin.core.commands.AdminUserLogoutCommand;
	import org.broadleafcommerce.admin.core.commands.GetAdminConfigCommand;
	import org.broadleafcommerce.admin.core.commands.GetAuthenticationCommand;
	import org.broadleafcommerce.admin.core.commands.InitializeApplicationCommand;
	import org.broadleafcommerce.admin.core.commands.LoadModulesCommand;
	import org.broadleafcommerce.admin.core.commands.codetype.AdminFindAllCodeTypesCommand;
	import org.broadleafcommerce.admin.core.commands.codetype.AdminSearchCodeTypesCommand;
	import org.broadleafcommerce.admin.core.control.events.AddModulesToViewEvent;
	import org.broadleafcommerce.admin.core.control.events.AdminUserLogoutEvent;
	import org.broadleafcommerce.admin.core.control.events.GetAdminConfigEvent;
	import org.broadleafcommerce.admin.core.control.events.GetAuthenticationEvent;
	import org.broadleafcommerce.admin.core.control.events.InitializeApplicationEvent;
	import org.broadleafcommerce.admin.core.control.events.LoadModulesEvent;
	import org.broadleafcommerce.admin.core.control.events.codetype.AdminFindAllCodeTypesEvent;
	import org.broadleafcommerce.admin.core.control.events.codetype.AdminSearchCodeTypesEvent;
	
	
	public class AdminController extends FrontController
	{
		public function AdminController()
		{
			super();
			addCommand(InitializeApplicationEvent.EVENT_INITIALIZE_APPLICATION, InitializeApplicationCommand);
			addCommand(GetAdminConfigEvent.EVENT_READ_ADMIN_CONFIG, GetAdminConfigCommand);
			addCommand(LoadModulesEvent.EVENT_LOAD_MODULES, LoadModulesCommand);
			addCommand(AddModulesToViewEvent.EVENT_ADD_MODULES_TO_VIEW, AddModulesToViewCommand);
			addCommand(GetAuthenticationEvent.EVENT_GET_AUTHENTICATION, GetAuthenticationCommand);
			
			addCommand(AdminFindAllCodeTypesEvent.EVENT_FIND_ALL_CODE_TYPES, AdminFindAllCodeTypesCommand);
			addCommand(AdminSearchCodeTypesEvent.EVENT_SEARCH_CODE_TYPES, AdminSearchCodeTypesCommand);
			addCommand(AdminUserLogoutEvent.EVENT_ADMIN_USER_LOGOUT, AdminUserLogoutCommand);
		}
		
	}
}