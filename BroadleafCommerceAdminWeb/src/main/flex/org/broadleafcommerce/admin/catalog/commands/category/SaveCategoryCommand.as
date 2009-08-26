package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	
	public class SaveCategoryCommand implements Command, IResponder{
		
		public function execute(event:CairngormEvent):void{
			trace("DEBUG: SaveCategoryCommand.execute()");
			var scce:SaveCategoryEvent = SaveCategoryEvent(event);
			var category:Category = scce.category;
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.saveCategory(category);
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