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
package org.broadleafcommerce.admin.catalog.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class StandardizeCatalogObjectsEvent extends CairngormEvent
	{
		public var categoryArray:ArrayCollection;
		public var productArray:ArrayCollection;
		public var skuArray:ArrayCollection;
		
		public static const EVENT_STANDARDIZE_CATALOG_OBJECTS:String = "standardize_catalog_objects_event";
		
		public function StandardizeCatalogObjectsEvent(categoryArray:ArrayCollection, productArray:ArrayCollection, skuArray:ArrayCollection)
		{
			super(EVENT_STANDARDIZE_CATALOG_OBJECTS);
			this.categoryArray = categoryArray;
			this.productArray = productArray;
			this.skuArray = skuArray;
			
		}
		
	}
}