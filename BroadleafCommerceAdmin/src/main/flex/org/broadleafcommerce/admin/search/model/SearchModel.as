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
package org.broadleafcommerce.admin.search.model
{
	import mx.collections.ArrayCollection;
	import org.broadleafcommerce.admin.search.vo.SearchIntercept;
	
	[Bindable]
	public class SearchModel
	{
		public function SearchModel()
		{
		}
		public var currentSearchIntercept:SearchIntercept = new SearchIntercept();

		public var searchInterceptList:ArrayCollection = new ArrayCollection();


		public static const SERVICE_ID:String = "blSearchService";

	}
}