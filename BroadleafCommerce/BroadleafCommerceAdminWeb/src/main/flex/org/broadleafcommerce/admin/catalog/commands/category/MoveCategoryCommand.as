package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.MoveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class MoveCategoryCommand implements Command
	{
		public function MoveCategoryCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: MoveCategoryCommand.execute()");			
			var mce:MoveCategoryEvent = MoveCategoryEvent(event);
			var movedCategory:Category = mce.movedCategory;
			var oldParent:Category = mce.oldParent;
			var newParent:Category = mce.newParent;
			
			for (var catIndex:String in movedCategory.allParentCategories){
				var cat:Category = Category(movedCategory.allParentCategories.getItemAt(parseInt(catIndex))); 
				if(cat.id == oldParent.id){
					movedCategory.allParentCategories.removeItemAt(parseInt(catIndex));
					break;
				} 
			}
			movedCategory.allParentCategories.addItem(newParent);
			
			var sce:SaveCategoryEvent = new SaveCategoryEvent(movedCategory);
			sce.dispatch();
		}
		
	}
}