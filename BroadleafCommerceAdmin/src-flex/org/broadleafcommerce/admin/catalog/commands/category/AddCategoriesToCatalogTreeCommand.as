package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.AddCategoriesToCatalogTreeEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class AddCategoriesToCatalogTreeCommand implements Command
	{
		
		public function AddCategoriesToCatalogTreeCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("AddCategoriesToCatalogTreeCommand.execute()");
			var actce:AddCategoriesToCatalogTreeEvent = AddCategoriesToCatalogTreeEvent(event);
			var rawCats:ArrayCollection = actce.categoryArray;
			var rootCats:ArrayCollection = actce.catalogTree;
			var subCatIds:ArrayCollection = new ArrayCollection();
			
			rootCats.removeAll();
			
			for (var i:String  in rawCats){
				var category:Category = rawCats[i];
//				category.allChildCategories.removeAll();
				if(category.defaultParentCategory != null){
					for each(var parentCategory:Category in category.allParentCategories){
						for each(var category2:Category in rawCats){
							if(parentCategory.id == category2.id){
								if(category2.allChildCategories == null){
									category2.allChildCategories = new ArrayCollection();								
								}
								category2.allChildCategories.addItem(category);
							}
						}			
						subCatIds.addItem(category.id);
					}
				}else{
					rootCats.addItem(category);
				}
			}
			if(CatalogModelLocator.getInstance().categoryModel.currentCategory != null){
				var ece:EditCategoryEvent = new EditCategoryEvent(CatalogModelLocator.getInstance().categoryModel.currentCategory);
				ece.dispatch();
			}
			
		}
		
	}
}