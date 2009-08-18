package org.broadleafcommerce.admin.catalog.vo.product
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.ProductWeight")]
	public class ProductWeight
	{
		public var weight:uint;
		public var unitOfMeasure:String;
	}
}