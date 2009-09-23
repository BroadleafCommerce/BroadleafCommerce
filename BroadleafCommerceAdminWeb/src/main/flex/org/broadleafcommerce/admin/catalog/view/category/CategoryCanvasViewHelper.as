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
package org.broadleafcommerce.admin.catalog.view.category
{
	import com.adobe.cairngorm.view.ViewHelper;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryTreeItem;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class CategoryCanvasViewHelper extends ViewHelper
	{
		public function CategoryCanvasViewHelper()
		{
			super();
		}
		
		public function selectCurrentCategoryInTree():void{
			var catalogModel:CatalogModel = CatalogModelLocator.getInstance().catalogModel;
			var  currentCategory:Category = CatalogModelLocator.getInstance().categoryModel.currentCategory;
			 
			if(currentCategory.id > 0){
				var ece:EditCategoryEvent = new EditCategoryEvent(CatalogModelLocator.getInstance().categoryModel.currentCategory);
				ece.dispatch();				
			}
			var branchItems:ArrayCollection = new ArrayCollection();
			for each(var cti:CategoryTreeItem in catalogModel.catalogTreeItemArray){
				if(cti.children != null && cti.children.length > 0){
					branchItems.addItem(cti);
				}
			}
			CategoryCanvas(this.view).categoryTreeCanvas.categoryTree.openItems = branchItems;
			
		}
		
	}
}