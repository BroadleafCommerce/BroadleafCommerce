package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.RemoveCrossSaleProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.vo.product.CrossSaleProduct;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class RemoveCrossSaleProductCommand implements Command
	{
		public function RemoveCrossSaleProductCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var rcspe:RemoveCrossSaleProductEvent = RemoveCrossSaleProductEvent(event);
			var crossProduct:CrossSaleProduct = rcspe.crossProduct;
			var product:Product = rcspe.product;
			for (var index:String in product.crossSaleProducts){
				var cp:CrossSaleProduct = CrossSaleProduct(product.crossSaleProducts[parseInt(index)]);
				if(cp.id == crossProduct.id){
					product.crossSaleProducts.removeItemAt(parseInt(index));
					break;
				}				
			}
			
			var spe:SaveProductEvent = new SaveProductEvent(product);
			spe.dispatch();
		}
		
	}
}