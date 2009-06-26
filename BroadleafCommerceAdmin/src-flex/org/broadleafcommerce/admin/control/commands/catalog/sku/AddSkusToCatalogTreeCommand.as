package org.broadleafcommerce.admin.control.commands.catalog.sku
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.control.events.catalog.sku.AddSkusToCatalogTreeEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;
	import org.broadleafcommerce.admin.model.data.remote.catalog.sku.Sku;

	public class AddSkusToCatalogTreeCommand implements Command
	{
		public function AddSkusToCatalogTreeCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var astct:AddSkusToCatalogTreeEvent = AddSkusToCatalogTreeEvent(event);
			var productsArray:ArrayCollection = astct.productArray;
			var skusArray:ArrayCollection = astct.skusArray;
			for(var i:String in skusArray){
				var sku:Sku = skusArray[i];
				for each(var product:Product in productsArray){
					for each(var skuParent:Product in sku.allParentProducts){
						if(product.id == skuParent.id){
							product.children.addItem(sku);
						}
					}
				}
			}
		}
		
	}
}