package org.broadleafcommerce.admin.catalog.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.StandardizeCatalogObjectsEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.AddCategoriesToCatalogTreeEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.AddProductsToCategoriesEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;

	public class BuildCatalogCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();
		
		public function BuildCatalogCommand()
		{
			var catalogTree:ArrayCollection = AppModelLocator.getInstance().catalogTree;
			var categoryArray:ArrayCollection = AppModelLocator.getInstance().categoryModel.categoryArray;
			var productsArray:ArrayCollection = AppModelLocator.getInstance().productModel.catalogProducts;
			var skusArray:ArrayCollection = AppModelLocator.getInstance().skuModel.catalogSkus;
			eventChain.addItem(new StandardizeCatalogObjectsEvent(categoryArray, productsArray, skusArray));
			eventChain.addItem(new AddCategoriesToCatalogTreeEvent(catalogTree, categoryArray));		
			// The following events add products and skus to the tree	 
			eventChain.addItem(new AddProductsToCategoriesEvent(categoryArray, productsArray));
			// eventChain.addItem(new AddSkusToProductsEvent(productsArray, skusArray));
																					  
		}

		public function execute(event:CairngormEvent):void
		{
			var categoriesArray:ArrayCollection = AppModelLocator.getInstance().categoryModel.categoryArray;
			var productsArray:ArrayCollection = AppModelLocator.getInstance().productModel.catalogProducts;
			var skusArray:ArrayCollection = AppModelLocator.getInstance().skuModel.catalogSkus;					

			if(categoriesArray.length > 0 && productsArray.length > 0 && skusArray.length > 0)
			{			
				for each(var event:CairngormEvent in eventChain){
					event.dispatch();
				}
			}
		}
		
	}
}