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
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.FindProductsByCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.vo.media.Media;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.model.ConfigModel;
	
	public class EditCategoryCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			trace("DEBUG: EditCategoryCommand.execute()");
			var ecce:EditCategoryEvent = EditCategoryEvent(event);
			var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel;
			var configModel:ConfigModel = AppModelLocator.getInstance().configModel; 
			 			
			categoryModel.currentCategory = ecce.category;
			categoryModel.categoryMedia = new ArrayCollection();			
			for (var x:String in ecce.category.categoryMedia){
				if(x is String && ecce.category.categoryMedia[x] is Media){
					var m:Media = new Media(); 
					m.id = Media(ecce.category.categoryMedia[x]).id;
					m.key = x;
					m.name = Media(ecce.category.categoryMedia[x]).name;
					m.label = Media(ecce.category.categoryMedia[x]).label;
					m.url = Media(ecce.category.categoryMedia[x]).url;
					categoryModel.categoryMedia.addItem(m);
				}
			}

			categoryModel.selectableParentCategories = categoryModel.currentCategory.allParentCategories;
			configModel.currentCodeTypes = categoryModel.categoryMediaCodes;			

			var fpbce:FindProductsByCategoryEvent = new FindProductsByCategoryEvent(ecce.category);
			fpbce.dispatch();

			categoryModel.viewState = CategoryModel.STATE_EDIT;

		}
	}
}