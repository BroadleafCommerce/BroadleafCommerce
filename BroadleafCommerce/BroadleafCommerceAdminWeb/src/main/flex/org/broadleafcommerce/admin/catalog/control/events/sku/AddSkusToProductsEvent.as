package org.broadleafcommerce.admin.catalog.control.events.sku
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;

	public class AddSkusToProductsEvent extends CairngormEvent
	{
		public static const EVENT_ADD_SKUS_TO_CATALOG_TREE:String = "add_skus_to_catalog_tree_event";

		public var productArray:ArrayCollection;
		public var skusArray:ArrayCollection;
		
		public function AddSkusToProductsEvent(products:ArrayCollection, skusArray:ArrayCollection)
		{
			super(EVENT_ADD_SKUS_TO_CATALOG_TREE);
			this.productArray = products;
			this.skusArray = skusArray;
		}
		
	}
}