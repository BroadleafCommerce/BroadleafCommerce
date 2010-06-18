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
package org.broadleafcommerce.admin.core.commands.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.universalmind.cairngorm.commands.Command;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.core.business.AdminToolsDelegate;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.model.ConfigModel;
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;
	
	public class AdminFindAllCodeTypesCommand extends Command
	{
		override public function execute(event:CairngormEvent):void{
			trace("DEBUG: AdminFindAllCodeTypesCommand.execute()");
			super.execute(event);
			var delegate:AdminToolsDelegate = new AdminToolsDelegate(this);
			delegate.findAllCodeTypes();
		}

		override public function result(data:Object):void{
			trace("DEBUG: AdminFindAllCodeTypesCommand.result()");
			// The following line is needed so that the remoteObject is properly 
			// created as a CodeType
			var codeType:CodeType = new CodeType();
			var event:ResultEvent = ResultEvent(data);
			var codes:ArrayCollection = ArrayCollection(event.result);
			var configModel:ConfigModel = AppModelLocator.getInstance().configModel;  
			
			configModel.codeTypes = codes;
			
		}
		
		override public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}		
	}
}