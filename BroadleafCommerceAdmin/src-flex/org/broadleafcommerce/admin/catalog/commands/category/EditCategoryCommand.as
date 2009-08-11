package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.FindProductsByCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	
	public class EditCategoryCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			var ecce:EditCategoryEvent = EditCategoryEvent(event);
			CatalogModelLocator.getInstance().categoryModel.currentCategory = ecce.category;					
			CatalogModelLocator.getInstance().categoryModel.viewState = CategoryModel.STATE_EDIT;
			var fpbce:FindProductsByCategoryEvent = new FindProductsByCategoryEvent(ecce.category);
			fpbce.dispatch();
		}
	}
}