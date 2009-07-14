package org.broadleafcommerce.admin.control.commands.catalog.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.utils.ObjectUtil;
	
	import org.broadleafcommerce.admin.control.events.catalog.category.AddCategoriesToCatalogTreeEvent;
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;

	public class AddCategoriesToCatalogTreeCommand implements Command
	{
		
		public function AddCategoriesToCatalogTreeCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
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
			
		}
		
	}
}