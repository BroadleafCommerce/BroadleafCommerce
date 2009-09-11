package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.CopyCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;

	public class CopyCategoryCommand implements Command, IResponder
	{
		public function CopyCategoryCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: CopyCategoryCommand.execute()");			
			var cce:CopyCategoryEvent = CopyCategoryEvent(event);
			cce.movedCategory.allParentCategories.addItem(cce.newParent);
			var sce:SaveCategoryEvent = new SaveCategoryEvent(cce.movedCategory);
			sce.dispatch();
			
		}
		
		public function result(data:Object):void{
			var facce:FindAllCategoriesEvent = new FindAllCategoriesEvent();
			facce.dispatch();
		}		
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);			
		}
		
	}
}