package org.broadleafcommerce.admin.catalog.model
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	
	public class CategoryTreeItem
	{
		
		public function CategoryTreeItem(category:Category){
			this.catId = category.id;
			this.name = category.name;
			this.category = category;
		}
		
		public var catId:int;
		public var name:String;
		public var category:Category;
		public var children:ArrayCollection = new ArrayCollection;
	}
}