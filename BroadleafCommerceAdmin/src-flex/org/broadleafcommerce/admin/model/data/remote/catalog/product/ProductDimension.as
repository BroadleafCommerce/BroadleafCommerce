package org.broadleafcommerce.admin.model.data.remote.catalog.product
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.ProductDimensionImpl")]
	public class ProductDimension
	{
		public var width:String;
		public var height:String;
		public var depth:String;
	}
}