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
	import com.universalmind.cairngorm.control.FrontController;
	
	import org.broadleafcommerce.admin.core.commands.RemoveAuthenticationCommand;
	import org.broadleafcommerce.admin.core.commands.GetAdminConfigCommand;
	import org.broadleafcommerce.admin.core.commands.GetAuthenticationCommand;
	import org.broadleafcommerce.admin.core.commands.InitializeApplicationCommand;
	import org.broadleafcommerce.admin.core.commands.LoadModulesCommand;
	import org.broadleafcommerce.admin.core.commands.codetype.AdminFindAllCodeTypesCommand;
	import org.broadleafcommerce.admin.core.commands.codetype.AdminSearchCodeTypesCommand;
	import org.broadleafcommerce.admin.core.control.events.RemoveAuthenticationEvent;
	import org.broadleafcommerce.admin.core.control.events.GetAdminConfigEvent;
	import org.broadleafcommerce.admin.core.control.events.GetAuthenticationEvent;
	import org.broadleafcommerce.admin.core.control.events.InitializeApplicationEvent;
	import org.broadleafcommerce.admin.core.control.events.LoadModulesEvent;
	import org.broadleafcommerce.admin.core.control.events.codetype.AdminFindAllCodeTypesEvent;
	import org.broadleafcommerce.admin.core.control.events.codetype.AdminSearchCodeTypesEvent;
	
	
	public class AdminController extends com.universalmind.cairngorm.control.FrontController
	{
		public function AdminController()
		{
			super();
			addCommand(InitializeApplicationEvent.EVENT_ID, InitializeApplicationCommand);
			addCommand(GetAdminConfigEvent.EVENT_ID, GetAdminConfigCommand);
			addCommand(LoadModulesEvent.EVENT_ID, LoadModulesCommand);
			addCommand(GetAuthenticationEvent.EVENT_ID, GetAuthenticationCommand);
			
			addCommand(AdminFindAllCodeTypesEvent.EVENT_ID, AdminFindAllCodeTypesCommand);
			addCommand(AdminSearchCodeTypesEvent.EVENT_ID, AdminSearchCodeTypesCommand);
			addCommand(RemoveAuthenticationEvent.EVENT_ID, RemoveAuthenticationCommand);
		}
		
	}
}