package org.broadleafcommerce.admin.view.catalog.category.components
{
	import mx.controls.treeClasses.TreeItemRenderer;
	
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class CategoryTreeItemRenderer extends TreeItemRenderer
	{
		public function CategoryTreeItemRenderer()
		{
			super();
		}
		
		override public function set data(value:Object):void{
			if(value is Category){
				super.data = value;
			}
		}
	}
}