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