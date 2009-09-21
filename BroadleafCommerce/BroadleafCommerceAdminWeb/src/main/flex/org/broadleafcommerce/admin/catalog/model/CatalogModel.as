package org.broadleafcommerce.admin.catalog.model
{
	import mx.collections.ArrayCollection;
	
	
	[Bindable]
	public class CatalogModel
	{
		public function CatalogModel()
		{
		}
		
		public static const SERVICE_ID:String = "blCatalogService";

		public static const STATE_VIEW_CATEGORY:String = "view_category_state";
		public static const STATE_VIEW_PRODUCT:String = "view_product_state";
		
		public var viewState:String = STATE_VIEW_CATEGORY; 
		
		public var catalogTree:ArrayCollection = new ArrayCollection();
		
		public var catalogTreeItemArray:ArrayCollection = new ArrayCollection();

		

	}
}