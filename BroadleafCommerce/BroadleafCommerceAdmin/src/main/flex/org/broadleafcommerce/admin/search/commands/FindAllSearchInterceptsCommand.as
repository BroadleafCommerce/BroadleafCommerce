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
package org.broadleafcommerce.admin.search.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.search.business.SearchServiceDelegate;
	import org.broadleafcommerce.admin.search.model.SearchModel;
	import org.broadleafcommerce.admin.search.model.SearchModelLocator;
	
	public class FindAllSearchInterceptsCommand implements Command, IResponder
	{
		private var searchModel:SearchModel = SearchModelLocator.getInstance().searchModel;
		
		public function execute(event:CairngormEvent):void
		{
			var delegate:SearchServiceDelegate = new SearchServiceDelegate(this);
			delegate.findAllSearchIntercepts();
		}
		
		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			this.searchModel.searchInterceptList = ArrayCollection(event.result);
		}			
		
		
		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+event);			
		}
		
	}
}