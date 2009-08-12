package org.broadleafcommerce.admin.catalog.model
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	
	[Bindable]
	public class CategoryModel
	{
		public static const STATE_NONE:String = "none";
		public static const STATE_NEW:String = "new_category";
		public static const STATE_EDIT:String = "edit_category";		
		
		public var viewType:String = "category";
		
		public var viewState:String = STATE_NONE;

		public var currentCategory:Category = new Category();

		public var categoryTree:ArrayCollection = new ArrayCollection();

		public var categoryArray:ArrayCollection = new ArrayCollection();
		
		public var categoryMedia:ArrayCollection = new ArrayCollection();
	}
}