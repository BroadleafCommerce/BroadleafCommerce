package org.broadleafcommerce.admin.tools.control
{
	import com.adobe.cairngorm.control.FrontController;
	
	import org.broadleafcommerce.admin.tools.commands.codetype.ClearCodeTypeSearchCommand;
	import org.broadleafcommerce.admin.tools.commands.codetype.CreateCodeTypeCommand;
	import org.broadleafcommerce.admin.tools.commands.codetype.EditCodeTypeCommand;
	import org.broadleafcommerce.admin.tools.commands.codetype.FindAllCodeTypesCommand;
	import org.broadleafcommerce.admin.tools.commands.codetype.RemoveCodeTypeCommand;
	import org.broadleafcommerce.admin.tools.commands.codetype.RemoveCodeTypeFormCommand;
	import org.broadleafcommerce.admin.tools.commands.codetype.SaveCodeTypeCommand;
	import org.broadleafcommerce.admin.tools.commands.codetype.SearchCodeTypesCommand;
	import org.broadleafcommerce.admin.tools.control.events.codetype.ClearCodeTypeSearchEvent;
	import org.broadleafcommerce.admin.tools.control.events.codetype.CreateCodeTypeEvent;
	import org.broadleafcommerce.admin.tools.control.events.codetype.EditCodeTypeEvent;
	import org.broadleafcommerce.admin.tools.control.events.codetype.FindAllCodeTypesEvent;
	import org.broadleafcommerce.admin.tools.control.events.codetype.RemoveCodeTypeEvent;
	import org.broadleafcommerce.admin.tools.control.events.codetype.RemoveCodeTypeFormEvent;
	import org.broadleafcommerce.admin.tools.control.events.codetype.SaveCodeTypeEvent;
	import org.broadleafcommerce.admin.tools.control.events.codetype.SearchCodeTypesEvent;

	public class ToolsController extends FrontController
	{
		public function ToolsController()
		{
			super();
			addCommand(FindAllCodeTypesEvent.EVENT_FIND_ALL_CODE_TYPES, FindAllCodeTypesCommand);
			addCommand(SaveCodeTypeEvent.EVENT_SAVE_CODE_TYPE, SaveCodeTypeCommand);
			addCommand(EditCodeTypeEvent.EVENT_EDIT_CODE_TYPE, EditCodeTypeCommand);
			addCommand(CreateCodeTypeEvent.EVENT_CREATE_CODE_TYPE, CreateCodeTypeCommand);
			addCommand(RemoveCodeTypeFormEvent.EVENT_REMOVE_CODE_TYPE_FORM, RemoveCodeTypeFormCommand);
			addCommand(SearchCodeTypesEvent.EVENT_SEARCH_CODE_TYPES, SearchCodeTypesCommand);
			addCommand(ClearCodeTypeSearchEvent.EVENT_CLEAR_CODE_TYPE_SEARCH, ClearCodeTypeSearchCommand);
			addCommand(RemoveCodeTypeEvent.EVENT_REMOVE_CODETYPE, RemoveCodeTypeCommand);
		}
		
	}
}