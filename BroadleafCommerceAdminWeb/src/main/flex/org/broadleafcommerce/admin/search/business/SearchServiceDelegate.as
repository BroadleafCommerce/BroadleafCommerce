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
package org.broadleafcommerce.admin.search.business
{
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.remoting.RemoteObject;
	
	import org.broadleafcommerce.admin.search.vo.SearchIntercept;

	public class SearchServiceDelegate
	{
        private var responder : IResponder;
        private var searchService:RemoteObject;

		public function SearchServiceDelegate(responder:IResponder)
		{
			this.searchService = SearchServiceLocator.getInstance().getService();
			this.responder = responder;
		}

 		public function findAllSearchIntercepts():void{
			var call:AsyncToken = searchService.getAllSearchIntercepts();
			call.addResponder(responder);
		}
		
		public function createSearchIntercept(intercept:SearchIntercept):void {
			var call:AsyncToken = searchService.createSearchIntercept(intercept);
			call.addResponder(responder);
		}
		
		public function updateSearchIntercept(intercept:SearchIntercept):void {
			var call:AsyncToken = searchService.updateSearchIntercept(intercept);
			call.addResponder(responder);
		}
		
		public function deleteSearchIntercept(intercept:SearchIntercept):void {
			var call:AsyncToken = searchService.deleteSearchIntercept(intercept);
			call.addResponder(responder);
		}
		
	}
}