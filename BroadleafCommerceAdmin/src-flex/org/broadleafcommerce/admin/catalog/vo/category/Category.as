package org.broadleafcommerce.admin.catalog.vo.category
{
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
		public var categoryImages:Object = new Object();
		public var longDescription:String;
		public var featuredProducts:ArrayCollection = new ArrayCollection();
		public var childCategories:ArrayCollection = new ArrayCollection();
		public var cachedChildCategoryUrlMap:Array;
		
		public function get children():ArrayCollection{
			return allChildCategories;
		}
		
		public function set children(newChildren:ArrayCollection):void{
			this.allChildCategories = newChildren;
		}
		
		public function get uid():String{
			return "Category_"+id;
		}
	}
}