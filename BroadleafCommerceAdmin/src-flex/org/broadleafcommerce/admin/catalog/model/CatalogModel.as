package org.broadleafcommerce.admin.catalog.model
{
	
	[Bindable]
	public class CatalogModel
	{
		public function CatalogModel()
		{
		}

		public static const STATE_VIEW_CATEGORY:String = "view_category_state";
		public static const STATE_VIEW_PRODUCT:String = "view_product_state";
		
		public var viewState:String = STATE_VIEW_CATEGORY; 

	}
}