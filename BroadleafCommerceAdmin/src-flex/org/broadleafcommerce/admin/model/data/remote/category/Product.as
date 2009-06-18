package org.broadleafcommerce.admin.model.data.remote
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.ProductImpl")]
	public class Product
	{
		public var id:Number;
		public var name:String;
		public var description:String;
		public var longDescription:String;
		public var activeStartDate:Date;
		public var activeEndDate:Date;
		public var model:String;
 		public var manufacturer:String;
		public var dimension:ProductDimension;
		public var weight:String;
		public var crossSaleProduct:ArrayCollection = new ArrayCollection();
		public var allSkus:ArrayCollection = new ArrayCollection();
		public var productImages:Object;
		public var defaultCategory:Category;
		public var allParentCategories:ArrayCollection();
		public var isFeaturedProduct:Boolean;
		public var skus:ArrayCollection = new ArrayCollection();
	}
}