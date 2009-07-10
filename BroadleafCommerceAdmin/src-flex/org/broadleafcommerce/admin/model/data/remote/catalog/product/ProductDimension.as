package org.broadleafcommerce.admin.model.data.remote.catalog.product
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.ProductDimension")]
	public class ProductDimension
	{
		public var width:uint;
		public var height:uint;
		public var depth:uint;
		public var girth:uint;
		public var size:String;
		public var container:String;
		
	}
}