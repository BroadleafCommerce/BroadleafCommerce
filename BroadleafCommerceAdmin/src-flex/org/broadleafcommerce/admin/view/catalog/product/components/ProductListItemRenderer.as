package org.broadleafcommerce.admin.view.catalog.product.components
{
	import mx.controls.listClasses.ListItemRenderer;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;

	public class ProductListItemRenderer extends ListItemRenderer
	{
		public function ProductListItemRenderer()
		{
			super();
		}

		override public function set data(value:Object):void{
			if(value is Product){
				super.data = value;
			}
		}
			
	}
}