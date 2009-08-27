package org.broadleafcommerce.admin.catalog.view.category
{
	import com.adobe.cairngorm.view.ViewHelper;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.view.category.components.CategoryTree;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class CategoryCanvasViewHelper extends ViewHelper
	{
		public function CategoryCanvasViewHelper()
		{
			super();
		}
		
		public function selectCurrentCategoryInTree():void{
			var ece:EditCategoryEvent = new EditCategoryEvent(CatalogModelLocator.getInstance().categoryModel.currentCategory);
			ece.dispatch();
			
		}
		
	}
}