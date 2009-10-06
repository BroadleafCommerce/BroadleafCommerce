package org.broadleafcommerce.admin.catalog.commands.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.AddSkuToProductEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;
	
	public class AddSkuToProductCommand implements Command 
	{
		public function AddSkuToProductCommand()
		{
		}
		
		public function execute(event:CairngormEvent):void{
			trace("DEBUG: execute : ");
			var currentProduct:Product = CatalogModelLocator.getInstance().productModel.currentProduct;
			var productSkus:ArrayCollection = CatalogModelLocator.getInstance().productModel.currentProduct.allSkus;
			var updatedSku:Sku = AddSkuToProductEvent(event).sku;
			//if the current sku is new
			if(updatedSku.id == -1){
				//add the sku to the product
				productSkus.addItem(updatedSku);
			}
			new SaveProductEvent(currentProduct).dispatch();
		}

	}
}