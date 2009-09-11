package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import mx.collections.ArrayCollection;
	import mx.utils.ObjectUtil;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.AddCategoriesToCatalogTreeEvent;
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
			var rawCats:ArrayCollection 
				= CatalogModelLocator.getInstance().categoryModel.categoryArray; 
				//= actce.categoryArray;
            var catTreeItems:ArrayCollection = 	CatalogModelLocator.getInstance().catalogTreeItemArray;
			var catTree:ArrayCollection = actce.catalogTree;
//			var subCatIds:ArrayCollection = new ArrayCollection();
			var parentQueue:Array = new Array();
			
			catTree.removeAll();
			
			for (var i:String  in rawCats){
				var category:Category = rawCats[i];
//				category.allChildCategories.removeAll();
				if(category.defaultParentCategory != null){
					for each(var parentCategory:Category in category.allParentCategories){
//						for each(var category2:CategoryTreeItem in catTreeItems){
						for each(var category2:Category in rawCats){
							if(parentCategory.id == category2.id){
//								if(category2.allChildCategories == null){
//									category2.allChildCategories = new ArrayCollection();								
//								}
								var catTreeItem:CategoryTreeItem = new CategoryTreeItem(category);
								for each(var treeCat:CategoryTreeItem in catTreeItems){
									if(treeCat.catId == category2.id){
										treeCat.children.addItem(catTreeItem);
									}
								}
								//category2.children.addItem(catTreeItem);
//								category2.allChildCategories.addItem(category);
							}
						}			
//						subCatIds.addItem(category.id);
					}
				}else{
					for each(var yac:CategoryTreeItem in catTreeItems){
						if(yac.catId == category.id){
							catTree.addItem(yac);
						}
					}
//					catTree.addItem(new CategoryTreeItem(category));
				}
			}

			var catMap:Object = new Object();
			for each(var qCat:CategoryTreeItem in catTreeItems){				
				for each(var qParentCat:Category in qCat.category.allParentCategories){
					var id:int = qParentCat.id;
					var cats:ArrayCollection = new ArrayCollection();
					if(id in catMap)
						cats = catMap[id];		
					var test:Object = ObjectUtil.copy(qCat);
					test.category = qCat.category;			
					cats.addItem(test);
					
					catMap[id] = cats;
				}
			}

			for each(var aCat:CategoryTreeItem in catTreeItems){
				var id2:int = aCat.catId;
				var children:ArrayCollection = catMap[aCat.catId];
				aCat.children = children;
			}

			var finaltree:ArrayCollection = new ArrayCollection();
			finaltree.addItem(catTreeItems[0]);
			CatalogModelLocator.getInstance().catalogTree = finaltree;
			CategoryCanvasViewHelper(ViewLocator.getInstance().getViewHelper("categoryCanvasViewHelper")).selectCurrentCategoryInTree();
			ProductCanvasViewHelper(ViewLocator.getInstance().getViewHelper("productCanvasViewHelper")).selectCurrentProduct();
		}
		
 		private function addCatToParents(cat:Category, children:ArrayCollection):void{
			for each(var childCat:Category in children){
			    if(cat.id == childCat.id)			    
					cat.children.addItem(childCat);
				addCatToParents(childCat, childCat.children);
					
			}
		}
		
	}
}