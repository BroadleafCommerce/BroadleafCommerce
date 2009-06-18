package org.broadleafcommerce.admin.model.data.remote
{
	import flash.utils.Dictionary;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.CategoryImpl")]
	public class Category
	{
		public var id:int;
		public var name:String;
		public var url:String;
		public var urlKey:String;
		public var defaultParentCategory:Category;
		public var description:String;
		public var activeStartDate:Date;
		public var activeEndDate:Date;
		public var displayTemplate:String;
		public var allChildCategories:ArrayCollection = new ArrayCollection();
		public var allParentCategories:ArrayCollection = new ArrayCollection();
		public var categoryImages:Object;
		public var longDescription:String;
		public var featuredProducts:ArrayCollection = new ArrayCollection();
		public var childCategories:ArrayCollection = new ArrayCollection();
		public var cachedChildCategoryUrlMap:Object;
	}
}