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
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.core.business.AdminToolsDelegate;
	import org.broadleafcommerce.admin.core.control.events.codetype.AdminSearchCodeTypesEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.model.ConfigModel;
	import org.broadleafcommerce.admin.core.model.AdminToolsModel;
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;

	public class AdminSearchCodeTypesCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var scte:AdminSearchCodeTypesEvent = AdminSearchCodeTypesEvent(event);
			var keyword:String = scte.keyword;
			var delegate:AdminToolsDelegate = new AdminToolsDelegate(this);
			delegate.lookupCodeTypeByKey(keyword);
		}
		
		public function result(data:Object):void{
			var configModel:ConfigModel = AppModelLocator.getInstance().configModel;
			var event:ResultEvent = ResultEvent(data);
			configModel.codeTypes = ArrayCollection(event.result);
		}		
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);			
		}
		
	}
}