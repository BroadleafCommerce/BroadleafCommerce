package org.broadleafcommerce.admin.model.data.catalog.category
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;

	public class TreeCategory extends Category
	{
		
		public var children:Array = new ArrayCollection();
		
		public function TreeCategory()
		{
			super();
		}
		
	}
}