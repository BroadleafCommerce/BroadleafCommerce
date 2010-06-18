package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.RemoveRelatedSaleProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.catalog.vo.product.RelatedProduct;

	public class RemoveRelatedSaleProductCommand implements Command
	{
		public function RemoveRelatedSaleProductCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var rcspe:RemoveRelatedSaleProductEvent = RemoveRelatedSaleProductEvent(event);

			var relatedProduct:RelatedProduct = rcspe.relatedProduct;
			var relatedCollectioName:String = rcspe.relatedCollectionName;
			var relatedCollection:ArrayCollection = (relatedProduct.product[relatedCollectioName] as ArrayCollection);

			for (var index:String in relatedCollection){
				var rp:RelatedProduct = RelatedProduct(relatedCollection[parseInt(index)]);
				if(rp.id == relatedProduct.id){
					relatedCollection.removeItemAt(parseInt(index));
					break;
				}				
			}
			
			var spe:SaveProductEvent = new SaveProductEvent(relatedProduct.product);
			spe.dispatch();
		}
		
	}
}