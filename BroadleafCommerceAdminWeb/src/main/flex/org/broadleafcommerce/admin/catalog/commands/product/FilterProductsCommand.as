package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.FilterProductsEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class FilterProductsCommand implements Command
	{
		public function FilterProductsCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: execute : ");
			var fpe:FilterProductsEvent = FilterProductsEvent(event);
			var filterString:String = fpe.filterString;
			var products:ArrayCollection = CatalogModelLocator.getInstance().productModel.catalogProducts;
			var filteredProducts:ArrayCollection = new ArrayCollection();
			if(filterString == ""){
				filteredProducts = products;	
			}else{				
				for each(var product:Product in products){
					if(product.name.indexOf(filterString) > -1){
						filteredProducts.addItem(product);
					}
				}
			}
			CatalogModelLocator.getInstance().productModel.filteredCatalogProducts = filteredProducts;
		}
		
	}
}