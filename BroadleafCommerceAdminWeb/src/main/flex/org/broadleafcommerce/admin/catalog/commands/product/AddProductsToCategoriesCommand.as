package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.AddProductsToCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class AddProductsToCategoriesCommand implements Command
	{
		public function AddProductsToCategoriesCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: execute : ");
			var aptcte:AddProductsToCategoriesEvent = AddProductsToCategoriesEvent(event);
			var categoryArray:ArrayCollection = aptcte.categoryArray;
			var productArray:ArrayCollection = aptcte.productsArray;			
			
			for (var i:String in productArray){
				var product:Product = productArray[i];
				for each(var parent:Category in product.allParentCategories){
					for each(var category:Category in categoryArray){
						if(category.id == parent.id){
//							category.children.addItem(product);						
						}
					}
					
				}
			}
		}
		
	}
}