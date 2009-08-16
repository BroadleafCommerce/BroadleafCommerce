package org.broadleafcommerce.admin.catalog.commands.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.sku.AddSkusToProductsEvent;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;

	public class AddSkusToProductsCommand implements Command
	{
		public function AddSkusToProductsCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var astct:AddSkusToProductsEvent = AddSkusToProductsEvent(event);
			var productsArray:ArrayCollection = astct.productArray;
			var skusArray:ArrayCollection = astct.skusArray;
			for(var i:String in skusArray){
				var sku:Sku = skusArray[i];
				for each(var product:Product in productsArray){
					for each(var skuParent:Product in sku.allParentProducts){
						if(product.id == skuParent.id){
							if(product.allSkus == null){
								product.allSkus = new ArrayCollection();
							}
							product.allSkus.addItem(sku);
						}
					}
				}
			}
		}
		
	}
}