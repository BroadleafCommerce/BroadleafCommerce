package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.RemoveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class RemoveCategoryCommand implements Command, IResponder
	{
		public function RemoveCategoryCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var rce:RemoveCategoryEvent = RemoveCategoryEvent(event);
			var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel;
			var productModel:ProductModel = CatalogModelLocator.getInstance().productModel;
			if(productModel.catalogProducts.length > 0){
				Alert.show("Category still contains products.  Please delete all products in category before deleting.");
				return;
			}
			for each(var category:Category in categoryModel.categoryArray){
				for each(var parentCategory:Category in category.allParentCategories){
					if(parentCategory.id == rce.category.id){
						Alert.show("Category still contains children.  Please delete children before deleting.");
						return;
					}
				}
			}
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.removeCategory(rce.category, rce.parentCategory);
		}
		
		public function result(data:Object):void
		{
			CatalogModelLocator.getInstance().categoryModel.currentCategory = new Category();
			var facce:FindAllCategoriesEvent = new FindAllCategoriesEvent();
			facce.dispatch();
		}
		
		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);			
		}
		
	}
}