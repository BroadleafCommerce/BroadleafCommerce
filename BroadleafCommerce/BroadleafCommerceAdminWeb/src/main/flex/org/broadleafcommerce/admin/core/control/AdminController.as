package org.broadleafcommerce.admin.core.control
{
	import com.adobe.cairngorm.control.FrontController;
	
	import org.broadleafcommerce.admin.core.commands.AddModulesToViewCommand;
	import org.broadleafcommerce.admin.core.commands.GetAdminConfigCommand;
	import org.broadleafcommerce.admin.core.commands.GetAuthenticationCommand;
	import org.broadleafcommerce.admin.core.commands.InitializeApplicationCommand;
	import org.broadleafcommerce.admin.core.commands.LoadModulesCommand;
	import org.broadleafcommerce.admin.core.commands.codetype.AdminFindAllCodeTypesCommand;
	import org.broadleafcommerce.admin.core.commands.codetype.AdminSearchCodeTypesCommand;
	import org.broadleafcommerce.admin.core.control.events.AddModulesToViewEvent;
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
			
		}
		
	}
}