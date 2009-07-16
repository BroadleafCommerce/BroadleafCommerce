package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	
	public class EditCategoryCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			var ecce:EditCategoryEvent = EditCategoryEvent(event);
			AppModelLocator.getInstance().categoryModel.currentCategory = ecce.category;					
			AppModelLocator.getInstance().categoryModel.viewState = CategoryModel.STATE_EDIT;

		}
	}
}