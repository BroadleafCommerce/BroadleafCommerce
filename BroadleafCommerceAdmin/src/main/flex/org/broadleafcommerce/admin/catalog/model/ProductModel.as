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
	
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	
	[Bindable]
	public class ProductModel
	{
		public static const STATE_VIEW_MEDIA:String = "product_media_view";
		public static const STATE_VIEW_EDIT:String = "product_edit_view";
		public static const STATE_VIEW_SKUS:String = "product_sku_view";
		
		public var viewState:String = STATE_VIEW_EDIT;

		public var currentProduct:Product = new Product();

		public var currentProductChanged:Boolean = false;
		
		public var catalogProducts:ArrayCollection = new ArrayCollection();
		
		public var allCatalogProducts:ArrayCollection = new ArrayCollection();

		public var allFilteredCatalogProducts:ArrayCollection = new ArrayCollection();

		public var filteredCatalogProducts:ArrayCollection = catalogProducts;
		
		public var selectedCategories:Array = new Array();
		
		public var productMedia:ArrayCollection = new ArrayCollection();
		
		public var productMediaCodes:ArrayCollection = new ArrayCollection();
		
	}
}