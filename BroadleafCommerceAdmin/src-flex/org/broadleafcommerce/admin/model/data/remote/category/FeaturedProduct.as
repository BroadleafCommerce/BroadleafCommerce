package org.broadleafcommerce.admin.model.data.remote
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.FeaturedProductImpl")]	
	public class FeaturedProduct
	{
		public var id:int;
		public var category:Category;
		public var product:Product;
		public var sequence:int;
		public var promotionMessage:String;
	}
}