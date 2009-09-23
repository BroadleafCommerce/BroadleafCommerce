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
	import com.adobe.cairngorm.view.ViewLocator;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.AddCategoriesToCatalogTreeEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryTreeItem;
	import org.broadleafcommerce.admin.catalog.view.category.CategoryCanvasViewHelper;
	import org.broadleafcommerce.admin.catalog.view.product.ProductCanvasViewHelper;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class AddCategoriesToCatalogTreeCommand implements Command
	{
		
		public function AddCategoriesToCatalogTreeCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: AddCategoriesToCatalogTreeCommand.execute()");
			var actce:AddCategoriesToCatalogTreeEvent = AddCategoriesToCatalogTreeEvent(event);
            var catalogModel:CatalogModel = CatalogModelLocator.getInstance().catalogModel; 

			// catalogModel.catalogTree = buildTreeFromParents(catalogModel.catalogTreeItemArray);
			catalogModel.catalogTree = buildTreeFromChildren(catalogModel.catalogTreeItemArray);
			CategoryCanvasViewHelper(ViewLocator.getInstance().getViewHelper("categoryCanvasViewHelper")).selectCurrentCategoryInTree();
			ProductCanvasViewHelper(ViewLocator.getInstance().getViewHelper("productCanvasViewHelper")).selectCurrentProduct();
		}
		
	
		private function buildTreeFromParents(catTreeItems:ArrayCollection):ArrayCollection{
			var catMap:Object = new Object();
			
			// For each categoryTreeItem in the category tree items array
			for each(var catTreeItemChild:CategoryTreeItem in catTreeItems){
				// then for each of the parents in a category tree item
				for each(var categoryParent:Category in catTreeItemChild.category.allParentCategories){
					// go back through the original category tree item array
					for each(var catTreeItemParent:CategoryTreeItem in catTreeItems){
						// to compare the two to see if they are the same and if they are
						if(catTreeItemParent.catId == categoryParent.id){
							// create a new pointer to the child
							var dupCatTreeItemChild:CategoryTreeItem = catTreeItemChild;
							// see if we have added that particular category tree item as a child yet			
							if(catTreeItemChild.catId in catMap){
								// if we have get it and duplicate it so that the tree will contain two distinct 
								// items as children
								var existingCatTreeItemChild:CategoryTreeItem = CategoryTreeItem(catMap[catTreeItemChild.catId]);								
								dupCatTreeItemChild = new CategoryTreeItem(existingCatTreeItemChild.category);
								// duplicate it's children so that the tree will reflect this
								dupCatTreeItemChild.children = dupChildren(existingCatTreeItemChild);
							}
							// add the child category tree item  to
							// the parent category tree item as a child
							catTreeItemParent.children.addItem(dupCatTreeItemChild); 	
							// put the category of the category tree item in a hashtable
							catTreeItemParent.children.sort;										
							// to see later if we have added it
							catMap[dupCatTreeItemChild.catId] = dupCatTreeItemChild;
						}
					}
				}
			}
			
			var finaltree:ArrayCollection = new ArrayCollection();
			finaltree.addItem(catTreeItems[0]);
			return finaltree;			
		}	
			
		private function dupChildren(categoryTreeItem:CategoryTreeItem):ArrayCollection{
			var returnAc:ArrayCollection = new ArrayCollection();
			if(categoryTreeItem.children.length > 0){
				for each(var cti:CategoryTreeItem in categoryTreeItem.children){					
					var newCTI:CategoryTreeItem = new CategoryTreeItem(cti.category);
					newCTI.children = dupChildren(cti);
					returnAc.addItem(newCTI);
				}
				return returnAc;
			}else{
				return returnAc;
			}
		}
	
		private function buildTreeFromChildren(catTreeItems:ArrayCollection):ArrayCollection{
			var catMap:Object = new Object();
			
			// For each categoryTreeItem in the category tree items array
			for each(var catTreeItem:CategoryTreeItem in catTreeItems){
				// then for each of the children in a category tree item
				for each(var category:Category in catTreeItem.category.allChildCategories){
					// go back through the original category tree item array
					for each(var catTreeItem2:CategoryTreeItem in catTreeItems){
						// to compare the two to see if they are the same and if they are
						if(catTreeItem2.catId == category.id){
							// create a new pointer to it
							var dupCatTreeItem2:CategoryTreeItem = catTreeItem2;
							// see if we have added that particular category tree item as a child yet			
							if(catTreeItem2.catId in catMap){
								// if we have duplicate it so that the tree will contain two distinct 
								// items as children
								dupCatTreeItem2 = new CategoryTreeItem(catTreeItem2.category);
							}
							// add the child category tree item (from allchildcategories) to
							// the original category tree item as a child 				
							catTreeItem.children.addItem(dupCatTreeItem2);
							// put the category of the category tree item in a hashtable
							// to see later if we have added it
							// NOTE: right now the actualy value of the key/value pair is not used
							catMap[catTreeItem2.catId] = catTreeItem2.category;
						}
					}
				}
			}
			
			var finaltree:ArrayCollection = new ArrayCollection();
			finaltree.addItem(catTreeItems[0]);
			return finaltree;			
		}	
	}
}