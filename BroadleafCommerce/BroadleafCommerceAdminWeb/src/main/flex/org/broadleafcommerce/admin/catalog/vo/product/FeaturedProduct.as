package org.broadleafcommerce.admin.catalog.vo.product
{
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	
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