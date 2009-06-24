package org.broadleafcommerce.admin.model.data.remote.catalog.sku
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.SkuImpl")]
	public class Sku
	{
		public var id:Number;
		public var salePrice:String;
		public var retailPrice:String;
		public var name:String;
		public var description:String;
		public var longDescription:String;
		public var taxable:String;
		public var discountable:String;
		public var available:String;
		public var activeStartDate:Date;
		public var activeEndDate:Date;
		public var skuImages:Object;
		public var allParentProducts:ArrayCollection = new ArrayCollection();
	}
}