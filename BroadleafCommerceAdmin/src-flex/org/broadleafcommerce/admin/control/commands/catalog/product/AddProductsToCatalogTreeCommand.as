package org.broadleafcommerce.admin.control.commands.catalog.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.control.events.catalog.product.AddProductsToCatalogTreeEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.AddSkusToCatalogTreeEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;

	public class AddProductsToCatalogTreeCommand implements Command
	{
		public function AddProductsToCatalogTreeCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var aptcte:AddProductsToCatalogTreeEvent = AddProductsToCatalogTreeEvent(event);
			var categoryArray:ArrayCollection = aptcte.categoryArray;
			var productArray:ArrayCollection = aptcte.productsArray;
			for (var i:String in productArray){
				var product:Product = productArray[i];
				for each(var category:Category in categoryArray){
					if(category.id == product.defaultCategory.id){
						category.children.addItem(product);						
					}
				}
			}
		}
		
	}
}