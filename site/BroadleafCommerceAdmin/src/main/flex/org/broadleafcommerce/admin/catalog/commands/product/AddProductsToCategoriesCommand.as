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
	
	import org.broadleafcommerce.admin.catalog.control.events.product.AddProductsToCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class AddProductsToCategoriesCommand implements Command
	{
		public function AddProductsToCategoriesCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: execute : ");
			var aptcte:AddProductsToCategoriesEvent = AddProductsToCategoriesEvent(event);
			var categoryArray:ArrayCollection = aptcte.categoryArray;
			var productArray:ArrayCollection = aptcte.productsArray;			
			
			for (var i:String in productArray){
				var product:Product = productArray[i];
				for each(var parent:Category in product.allParentCategories){
					for each(var category:Category in categoryArray){
						if(category.id == parent.id){
//							category.children.addItem(product);						
						}
					}
					
				}
			}
		}
		
	}
}