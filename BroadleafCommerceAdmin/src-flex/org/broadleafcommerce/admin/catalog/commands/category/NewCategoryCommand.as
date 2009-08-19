package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.NewCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	
	public class NewCategoryCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			trace("execute : ");
			var nce:NewCategoryEvent = NewCategoryEvent(event);
			CatalogModelLocator.getInstance().categoryModel.currentCategory = new Category();
			if(nce.parentCategory != null){
				CatalogModelLocator.getInstance().categoryModel.currentCategory.allParentCategories.addItem(nce.parentCategory);				
			}
//			else{
//				AppModelLocator.getInstance().categoryModel.currentCategory.allParentCategories.addItem(AppModelLocator.getInstance().catalogTree.getItemAt(0));
//			}
			
			CatalogModelLocator.getInstance().categoryModel.viewState = CategoryModel.STATE_NEW;
		}
	}
}