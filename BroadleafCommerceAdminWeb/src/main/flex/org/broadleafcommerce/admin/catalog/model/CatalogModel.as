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
package org.broadleafcommerce.admin.catalog.model
{
	import mx.collections.ArrayCollection;
	
	
	[Bindable]
	public class CatalogModel
	{
		public function CatalogModel()
		{
		}
		
		public static const SERVICE_ID:String = "blCatalogService";

		public static const STATE_VIEW_CATEGORY:String = "view_category_state";
		public static const STATE_VIEW_PRODUCT:String = "view_product_state";
		
		public var viewState:String = STATE_VIEW_CATEGORY; 
		
		public var catalogTree:ArrayCollection = new ArrayCollection();
		
		public var catalogTreeItemArray:ArrayCollection = new ArrayCollection();

		

	}
}