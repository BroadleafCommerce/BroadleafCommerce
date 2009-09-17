package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.responder.CairngormResponder;
	
	import flash.events.Event;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.MoveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class MoveCategoryCommand implements Command
	{
		public function MoveCategoryCommand()
		{
		}

		private var oldParent:Category;
		private var newParent:Category;

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: MoveCategoryCommand.execute()");			
			var mce:MoveCategoryEvent = MoveCategoryEvent(event);
			var movedCategory:Category = mce.movedCategory;
			oldParent = mce.oldParent;
			newParent = mce.newParent;
			
			for (var catIndex:String in movedCategory.allParentCategories){
				var cat:Category = Category(movedCategory.allParentCategories.getItemAt(parseInt(catIndex))); 
				if(cat.id == oldParent.id){
					movedCategory.allParentCategories.removeItemAt(parseInt(catIndex));
					break;
				} 
			}
			movedCategory.allParentCategories.addItem(newParent);
			movedCategory.defaultParentCategory = newParent;
			
			
			for (var i:String in oldParent.allChildCategories){
				var childCat:Category = Category(oldParent.allChildCategories[i]);
				if(childCat.id == movedCategory.id){
					oldParent.allChildCategories.removeItemAt(parseInt(i));
					break;
				}
			}

			var sce:SaveCategoryEvent = new SaveCategoryEvent(movedCategory);			
			sce.dispatch();
		}
		
	}
}