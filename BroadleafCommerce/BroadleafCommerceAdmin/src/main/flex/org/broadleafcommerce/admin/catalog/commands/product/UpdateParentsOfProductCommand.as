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
package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.UpdateParentsOfProductEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class UpdateParentsOfProductCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: UpdateParentsOfProductCommand.execute()");
			var aptpe:UpdateParentsOfProductEvent = UpdateParentsOfProductEvent(event);
			aptpe.product.allParentCategories = aptpe.parents;
			CatalogModelLocator.getInstance().productModel.currentProductChanged = true;
			var allCategories:ArrayCollection = CatalogModelLocator.getInstance().categoryModel.categoryArray;
			for each(var category:Category in allCategories){
				if(aptpe.product.allParentCategories.contains(category)){
					category.allChildCategories.addItem(aptpe.product);
				}else{
					for (var index:String in category.allChildCategories){
						var childObj:Object = category.allChildCategories.getItemAt(int(index)); 
						if(childObj is Product && Product(childObj) == aptpe.product){
							category.allChildCategories.removeItemAt(int(index));
						}
					}
				}
			} 
//			for each(var parent:Object in aptpe.parents){
//				if(parent is Category){
//					Category(parent).allChildCategories.addItem(aptpe.product);
//				}
//			}
		}
		
	}
}