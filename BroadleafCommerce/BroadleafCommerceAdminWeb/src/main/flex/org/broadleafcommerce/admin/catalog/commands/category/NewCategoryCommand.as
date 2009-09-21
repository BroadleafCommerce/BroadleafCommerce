package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.NewCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	
	public class NewCategoryCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			trace("DEBUG: NewCategoryCommand.execute() ");
			var nce:NewCategoryEvent = NewCategoryEvent(event);
			var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel;  
			categoryModel.currentCategory = new Category();
			categoryModel.selectableParentCategories = categoryModel.categoryArray;
			categoryModel.viewState = CategoryModel.STATE_NEW;
		}
	}
}