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
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;
	import org.broadleafcommerce.admin.tools.business.ToolsServiceDelegate;
	import org.broadleafcommerce.admin.tools.control.events.codetype.SearchCodeTypesEvent;
	import org.broadleafcommerce.admin.tools.model.CodeTypeModel;
	import org.broadleafcommerce.admin.tools.model.ToolsModelLocator;

	public class SearchCodeTypesCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var scte:SearchCodeTypesEvent = SearchCodeTypesEvent(event);
			var keyword:String = scte.keyword;
			var delegate:ToolsServiceDelegate = new ToolsServiceDelegate(this);
			delegate.lookupCodeTypeByKey(keyword);
		}
		
		public function result(data:Object):void{
			var toolsModel:CodeTypeModel = ToolsModelLocator.getInstance().codeTypeModel;
			var event:ResultEvent = ResultEvent(data);
			toolsModel.codeTypes = ArrayCollection(event.result);
			if(toolsModel.codeTypes.length != 0){
				toolsModel.currentCodeType = CodeType(toolsModel.codeTypes.getItemAt(0));
				if(toolsModel.currentCodeType.modifiable == 'true'){
					toolsModel.viewState = CodeTypeModel.STATE_EDIT;
				}else{
					toolsModel.viewState = CodeTypeModel.STATE_VIEW;
				}
			}else{
				toolsModel.viewState = CodeTypeModel.STATE_NONE;
			}
		}		
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);			
		}
		
	}
}