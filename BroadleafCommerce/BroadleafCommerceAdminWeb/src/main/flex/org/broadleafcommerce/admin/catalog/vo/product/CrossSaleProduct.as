package org.broadleafcommerce.admin.catalog.vo.product
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.CrossSaleProductImpl")]	
	public class CrossSaleProduct
	{
		public var id:int;
		public var product:Product;
		public var relatedProduct:Product;
		public var sequence:int;
		public var promotionMessage:String;
	}
}