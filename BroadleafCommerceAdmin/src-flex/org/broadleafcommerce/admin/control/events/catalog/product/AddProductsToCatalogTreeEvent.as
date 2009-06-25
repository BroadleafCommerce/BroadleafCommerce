package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class AddProductsToCatalogTreeEvent extends CairngormEvent
	{
		public static const EVENT_ADD_PRODUCTS_TO_CATALOG_TREE:String = "add_products_to_catalog_tree_event";
		
		public function AddProductsToCatalogTreeEvent()
		{
			super(EVENT_ADD_PRODUCTS_TO_CATALOG_TREE);
		}
		
	}
}