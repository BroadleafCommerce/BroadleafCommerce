package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.FindProductsByCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.vo.Media;
	
	public class EditCategoryCommand implements Command
	{
		public function execute(event:CairngormEvent):void{
			var ecce:EditCategoryEvent = EditCategoryEvent(event);
			var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel; 			
			categoryModel.currentCategory = ecce.category;
			categoryModel.categoryMedia = new ArrayCollection();
			for (var x:String in ecce.category.categoryMedia){
				if(x is String && ecce.category.categoryMedia[x] is Media){
					var m:Media = Media(ecce.category.categoryMedia[x]);
					m.key = x;
					categoryModel.categoryMedia.addItem(m);
				}
			}

			categoryModel.viewState = CategoryModel.STATE_EDIT;
			var fpbce:FindProductsByCategoryEvent = new FindProductsByCategoryEvent(ecce.category);
			fpbce.dispatch();

		}
	}
}