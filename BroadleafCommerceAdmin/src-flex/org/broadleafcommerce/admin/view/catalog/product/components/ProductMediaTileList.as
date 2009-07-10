package org.broadleafcommerce.admin.view.catalog.product.components
{
	import mx.collections.ArrayCollection;
	import mx.controls.TileList;
	
	import org.broadleafcommerce.admin.model.view.ProductMedia;

	public class ProductMediaTileList extends TileList
	{
		public function ProductMediaTileList()
		{
			super();
		}
		
		override public function set dataProvider(value:Object):void{
			var newDataProvider:ArrayCollection = new ArrayCollection();
			for(var P in value){
				if(P is String){
					if(value[P] is String){
						var productMedia:ProductMedia = new ProductMedia();
						productMedia.label = P;
						productMedia.source = value[P];
						newDataProvider.addItem(productMedia);
//						ArrayCollection(this.dataProvider).addItem(productMedia);
					}
				}
			}
			super.dataProvider = newDataProvider;
		}
		
	}
}