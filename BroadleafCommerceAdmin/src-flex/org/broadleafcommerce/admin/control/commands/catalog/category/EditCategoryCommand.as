package org.broadleafcommerce.admin.control.commands.catalog.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.control.events.catalog.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;
	import org.broadleafcommerce.admin.model.view.CategoryModel;
	
	public class EditCategoryCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			var ecce:EditCategoryEvent = EditCategoryEvent(event);
			AppModelLocator.getInstance().categoryModel.currentCategory = ecce.category;					
			AppModelLocator.getInstance().categoryModel.viewState = CategoryModel.STATE_EDIT;

		}
	}
}