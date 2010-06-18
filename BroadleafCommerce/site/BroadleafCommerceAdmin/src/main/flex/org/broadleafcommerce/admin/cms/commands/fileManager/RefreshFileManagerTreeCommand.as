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
package org.broadleafcommerce.admin.cms.commands.fileManager
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;
	
	import org.broadleafcommerce.admin.cms.control.events.RefreshFileManagerTreeEvent;
	import org.broadleafcommerce.admin.cms.model.ContentModelLocator;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;

	public class RefreshFileManagerTreeCommand implements ICommand, IResponder
	{
		private static const FILE_LIST:String = AppModelLocator.getInstance().configModel.urlServer+"spring/ls";

		public function execute(event:CairngormEvent):void
		{
			var rfm:RefreshFileManagerTreeEvent = event as RefreshFileManagerTreeEvent;
			var httpService:HTTPService = new HTTPService();
			httpService.resultFormat = "e4x";
			httpService.url = FILE_LIST;

			var call:AsyncToken = httpService.send();
			call.addResponder(this);
		}

		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			var xmlFileList:XML = event.result as XML;
			ContentModelLocator.getInstance().contentModel.fileManagerTree = xmlFileList;
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}