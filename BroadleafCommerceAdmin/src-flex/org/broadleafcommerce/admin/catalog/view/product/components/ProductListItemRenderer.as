package org.broadleafcommerce.admin.catalog.view.product.components
{
	import mx.controls.listClasses.ListItemRenderer;
	
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class ProductListItemRenderer extends ListItemRenderer
	{
		public function ProductListItemRenderer()
		{
			super();
		}

		override public function set data(value:Object):void{
			if(value is Product){
				super.data = value;
			}else{
				super.data = null;
			}
		}
			
	}
}