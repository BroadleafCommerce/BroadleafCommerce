package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.UpdateParentsOfProductEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class UpdateParentsOfProductCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: UpdateParentsOfProductCommand.execute()");
			var aptpe:UpdateParentsOfProductEvent = UpdateParentsOfProductEvent(event);
			aptpe.product.allParentCategories = aptpe.parents;
			CatalogModelLocator.getInstance().productModel.currentProductChanged = true;
			var allCategories:ArrayCollection = CatalogModelLocator.getInstance().categoryModel.categoryArray;
			for each(var category:Category in allCategories){
				if(aptpe.product.allParentCategories.contains(category)){
					category.allChildCategories.addItem(aptpe.product);
				}else{
					for (var index:String in category.allChildCategories){
						var childObj:Object = category.allChildCategories.getItemAt(int(index)); 
						if(childObj is Product && Product(childObj) == aptpe.product){
							category.allChildCategories.removeItemAt(int(index));
						}
					}
				}
			} 
//			for each(var parent:Object in aptpe.parents){
//				if(parent is Category){
//					Category(parent).allChildCategories.addItem(aptpe.product);
//				}
//			}
		}
		
	}
}