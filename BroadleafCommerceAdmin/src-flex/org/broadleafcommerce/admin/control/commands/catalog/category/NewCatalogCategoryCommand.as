package org.broadleafcommerce.admin.control.commands.catalog.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;
	import org.broadleafcommerce.admin.model.view.CategoryModel;
	
	public class NewCatalogCategoryCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			AppModelLocator.getInstance().categoryModel.currentCategory = new Category();
			AppModelLocator.getInstance().categoryModel.viewState = CategoryModel.STATE_NEW;
		}
	}
}