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
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.model.IModelLocator;
	
	import mx.collections.ArrayCollection;

	public class CatalogModelLocator implements IModelLocator
	{
		private static var modelLocator:CatalogModelLocator;


		public static function getInstance():CatalogModelLocator
		{
			if(modelLocator == null)
				modelLocator = new CatalogModelLocator();

			return modelLocator;
		}

		public function CatalogModelLocator()
		{
			if(modelLocator != null)
				throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "BlcAdminModelLocator");
		}

		[Bindable]
		public var catalogModel:CatalogModel = new CatalogModel();

		[Bindable]
		public var categoryModel:CategoryModel = new CategoryModel();

		[Bindable]
		public var productModel:ProductModel = new ProductModel();

		[Bindable]
		public var skuModel:SkuModel = new SkuModel();

		[Bindable]
		public var mediaModel:MediaModel = new MediaModel();

		[Bindable]
		public var categoriesSelected:Boolean = true;

		[Bindable]
		public var productsSelected:Boolean = false;

	}
}