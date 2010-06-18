package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.AddRelatedSaleProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.product.RelatedProduct;

	public class AddRelatedSaleProductCommand implements Command
	{
		public function AddRelatedSaleProductCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var acspe:AddRelatedSaleProductEvent = AddRelatedSaleProductEvent(event);
			
			var relatedProduct:RelatedProduct = acspe.relatedProduct;
			var relatedCollectionName:String = acspe.relatedCollectionName;

			(relatedProduct.product[relatedCollectionName] as ArrayCollection).addItemAt(relatedProduct, acspe.index);
			
			var spe:SaveProductEvent = new SaveProductEvent(relatedProduct.product);
			spe.dispatch();
		}
		
	}
}