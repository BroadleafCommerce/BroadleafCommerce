package org.broadleafcommerce.admin.model.view
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;
	
	[Bindable]
	public class ProductModel
	{
		public static const STATE_VIEW_MEDIA:String = "product_media_view";
		public static const STATE_VIEW_EDIT:String = "product_edit_view";
		public static const STATE_VIEW_SKUS:String = "product_sku_view";
		
		public var viewState:String = STATE_VIEW_EDIT;

		public var currentProduct:Product = new Product();

		public var currentProductChanged:Boolean = false;
		
		public var catalogProducts:ArrayCollection = new ArrayCollection();
	}
}