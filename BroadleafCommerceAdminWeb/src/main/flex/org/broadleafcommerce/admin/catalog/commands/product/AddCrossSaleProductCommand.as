package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.AddCrossSaleProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.vo.product.CrossSaleProduct;

	public class AddCrossSaleProductCommand implements Command
	{
		public function AddCrossSaleProductCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var acspe:AddCrossSaleProductEvent = AddCrossSaleProductEvent(event);
			
			var crossSaleProduct:CrossSaleProduct = new CrossSaleProduct();
			crossSaleProduct.product = acspe.product;
			crossSaleProduct.relatedProduct = acspe.crossProduct;
			crossSaleProduct.promotionMessage = "";

			if(acspe.index > -1){
				crossSaleProduct.sequence = acspe.index;
				acspe.product.crossSaleProducts.addItemAt(crossSaleProduct, acspe.index);
			}else{
				crossSaleProduct.sequence = acspe.product.crossSaleProducts.length;
				acspe.product.crossSaleProducts.addItem(crossSaleProduct);
				
			}
			
			var spe:SaveProductEvent = new SaveProductEvent(acspe.product);
			spe.dispatch();
		}
		
	}
}