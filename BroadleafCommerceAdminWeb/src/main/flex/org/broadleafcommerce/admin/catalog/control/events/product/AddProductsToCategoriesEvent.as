package org.broadleafcommerce.admin.catalog.control.events.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;

	public class AddProductsToCategoriesEvent extends CairngormEvent
	{
		public static const EVENT_ADD_PRODUCTS_TO_CATALOG_TREE:String = "add_products_to_catalog_tree_event";
		
		public var categoryArray:ArrayCollection;
		public var productsArray:ArrayCollection;
		
		public function AddProductsToCategoriesEvent(categories:ArrayCollection, products:ArrayCollection)
		{
			super(EVENT_ADD_PRODUCTS_TO_CATALOG_TREE);
			this.categoryArray = categories;
			this.productsArray = products;
		}
		
	}
}