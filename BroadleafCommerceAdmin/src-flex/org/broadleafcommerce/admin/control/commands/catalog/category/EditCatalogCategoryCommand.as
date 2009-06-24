package org.broadleafcommerce.admin.control.commands.catalog.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.category.EditCatalogCategoryEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.view.CategoryModel;
	
	public class EditCatalogCategoryCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			var ecce:EditCatalogCategoryEvent = EditCatalogCategoryEvent(event);
			AppModelLocator.getInstance().categoryModel.currentCategory = ecce.category;
			AppModelLocator.getInstance().categoryModel.viewState = CategoryModel.STATE_EDIT;

		}
	}
}