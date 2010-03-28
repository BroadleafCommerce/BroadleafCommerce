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
package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.RemoveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class RemoveCategoryCommand implements Command, IResponder
	{
		public function RemoveCategoryCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var rce:RemoveCategoryEvent = RemoveCategoryEvent(event);
			var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel;
			var productModel:ProductModel = CatalogModelLocator.getInstance().productModel;
			if(productModel.catalogProducts.length > 0){
				Alert.show("Category still contains products.  Please delete all products in category before deleting.");
				return;
			}
			for each(var category:Category in categoryModel.categoryArray){
				for each(var parentCategory:Category in category.allParentCategories){
					if(parentCategory.id == rce.category.id){
						Alert.show("Category still contains children.  Please delete children before deleting.");
						return;
					}
				}
			}
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.removeCategory(rce.category, rce.parentCategory);
		}
		
		public function result(data:Object):void
		{
			CatalogModelLocator.getInstance().categoryModel.currentCategory = new Category();
			var facce:FindAllCategoriesEvent = new FindAllCategoriesEvent();
			facce.dispatch();
		}
		
		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);			
		}
		
	}
}