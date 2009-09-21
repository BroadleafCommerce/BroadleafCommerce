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
package org.broadleafcommerce.admin.tools.commands.codetype
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.tools.control.events.codetype.EditCodeTypeEvent;
	import org.broadleafcommerce.admin.tools.model.CodeTypeModel;
	import org.broadleafcommerce.admin.tools.model.ToolsModelLocator;

	public class EditCodeTypeCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			var ecte:EditCodeTypeEvent = EditCodeTypeEvent(event);
			var toolsModel:CodeTypeModel = ToolsModelLocator.getInstance().codeTypeModel;
			toolsModel.currentCodeType = ecte.codeType;
			if(toolsModel.currentCodeType.modifiable == "true"){
				toolsModel.viewState = CodeTypeModel.STATE_EDIT;
			}else{
				toolsModel.viewState = CodeTypeModel.STATE_VIEW;
			}
		}
	}
}