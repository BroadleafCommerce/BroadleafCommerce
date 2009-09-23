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