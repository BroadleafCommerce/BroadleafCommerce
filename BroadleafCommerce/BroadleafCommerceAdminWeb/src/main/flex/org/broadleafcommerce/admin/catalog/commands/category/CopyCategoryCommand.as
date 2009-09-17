package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.CopyCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;

	public class CopyCategoryCommand implements Command
	{
		public function CopyCategoryCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: CopyCategoryCommand.execute()");			
			var cce:CopyCategoryEvent = CopyCategoryEvent(event);
			cce.movedCategory.allParentCategories.addItem(cce.newParent);
			cce.newParent.allChildCategories.addItem(cce.movedCategory);
			var saveNewParentEvent:SaveCategoryEvent = new SaveCategoryEvent(cce.newParent);
			var sce:SaveCategoryEvent = new SaveCategoryEvent(cce.movedCategory, saveNewParentEvent);
			sce.dispatch();
			
		}
		
	}
}