package org.broadleafcommerce.admin.model.data.remote.catalog.product
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.ProductWeight")]
	public class ProductWeight
	{
		public var weight:uint;
		public var unitOfMeasure:String;
	}
}