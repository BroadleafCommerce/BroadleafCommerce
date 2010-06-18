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
package org.broadleafcommerce.admin.search.control
{
	import com.adobe.cairngorm.control.FrontController;
	import org.broadleafcommerce.admin.search.commands.CreateSearchInterceptCommand;
	import org.broadleafcommerce.admin.search.commands.DeleteSearchInterceptCommand;
	import org.broadleafcommerce.admin.search.commands.FindAllSearchInterceptsCommand;
	import org.broadleafcommerce.admin.search.commands.UpdateSearchInterceptCommand;
	import org.broadleafcommerce.admin.search.control.events.CreateSearchInterceptEvent;
	import org.broadleafcommerce.admin.search.control.events.DeleteSearchInterceptEvent;
	import org.broadleafcommerce.admin.search.control.events.FindAllSearchInterceptsEvent;
	import org.broadleafcommerce.admin.search.control.events.UpdateSearchInterceptEvent;
	
	public class SearchController extends FrontController
	{
		public function SearchController()
		{
			super();
			addCommand(FindAllSearchInterceptsEvent.EVENT_FIND_ALL_SEARCH_INTERCEPTS, FindAllSearchInterceptsCommand);
			addCommand(CreateSearchInterceptEvent.EVENT_CREATE_SEARCH_INTERCEPT, CreateSearchInterceptCommand);
			addCommand(UpdateSearchInterceptEvent.EVENT_UPDATE_SEARCH_INTERCEPT, UpdateSearchInterceptCommand);
			addCommand(DeleteSearchInterceptEvent.EVENT_DELETE_SEARCH_INTERCEPT, DeleteSearchInterceptCommand);
		}

	}
}