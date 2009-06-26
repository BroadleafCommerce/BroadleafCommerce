package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;

	public class AddProductsToCatalogTreeEvent extends CairngormEvent
	{
		public static const EVENT_ADD_PRODUCTS_TO_CATALOG_TREE:String = "add_products_to_catalog_tree_event";
		
		public var categoryArray:ArrayCollection;
		public var productsArray:ArrayCollection;
		
		public function AddProductsToCatalogTreeEvent(categories:ArrayCollection, products:ArrayCollection)
		{
			super(EVENT_ADD_PRODUCTS_TO_CATALOG_TREE);
			this.categoryArray = categories;
			this.productsArray = products;
		}
		
	}
}