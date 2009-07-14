package org.broadleafcommerce.admin.control.commands.catalog.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.category.NewCategoryEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;
	import org.broadleafcommerce.admin.model.view.CategoryModel;
	
	public class NewCategoryCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			var nce:NewCategoryEvent = NewCategoryEvent(event);
			AppModelLocator.getInstance().categoryModel.currentCategory = new Category();
			if(nce.parentCategory == null){
				AppModelLocator.getInstance().categoryModel.currentCategory.allParentCategories.addItem(AppModelLocator.getInstance().catalogTree.getItemAt(0));
			}else{
				AppModelLocator.getInstance().categoryModel.currentCategory.allParentCategories.addItem(nce.parentCategory);				
			}
			
			AppModelLocator.getInstance().categoryModel.viewState = CategoryModel.STATE_NEW;
		}
	}
}