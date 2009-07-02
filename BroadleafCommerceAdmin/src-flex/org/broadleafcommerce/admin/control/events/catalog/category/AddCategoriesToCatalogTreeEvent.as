package org.broadleafcommerce.admin.control.events.catalog.category
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;

	public class AddCategoriesToCatalogTreeEvent extends CairngormEvent
	{
		public static const EVENT_ADD_CATEGORIES_TO_CATALOG_TREE:String = "add_categories_to_catalog_tree_event";
		
		public var catalogTree:ArrayCollection;
		public var categoryArray:ArrayCollection;

		public function AddCategoriesToCatalogTreeEvent(catalogTree:ArrayCollection, categoryArray:ArrayCollection)
		{
			super(EVENT_ADD_CATEGORIES_TO_CATALOG_TREE);
			this.catalogTree = catalogTree;
			this.categoryArray = categoryArray;
		}
		
	}
}