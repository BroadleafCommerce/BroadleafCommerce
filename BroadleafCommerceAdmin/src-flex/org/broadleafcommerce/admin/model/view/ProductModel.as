package org.broadleafcommerce.admin.model.view
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;
	
	[Bindable]
	public class ProductModel
	{
		public static const STATE_NONE:String = "none";
		public static const STATE_NEW:String = "new_category";
		public static const STATE_EDIT:String = "edit_category";
		public static const STATE_VIEW:String = "view";
		
		public var viewState:String = STATE_NONE;

		public var currentProduct:Product = new Product();
		
		public var catalogProducts:ArrayCollection = new ArrayCollection();
	}
}