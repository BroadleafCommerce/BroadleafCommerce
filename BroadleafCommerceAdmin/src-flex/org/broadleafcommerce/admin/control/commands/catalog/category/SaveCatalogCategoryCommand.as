package org.broadleafcommerce.admin.control.commands.catalog.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.control.events.catalog.category.FindAllCatalogCategoriesEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.SaveCatalogCategoryEvent;
	import org.broadleafcommerce.admin.model.business.BroadleafCommerceAdminServiceDelegate;
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;
	
	public class SaveCatalogCategoryCommand implements Command, IResponder{
		
		public function execute(event:CairngormEvent):void{
			var scce:SaveCatalogCategoryEvent = SaveCatalogCategoryEvent(event);
			var category:Category = scce.category;
			var delegate:BroadleafCommerceAdminServiceDelegate = new BroadleafCommerceAdminServiceDelegate(this);
			delegate.saveCategory(category);
		}
		
		public function result(data:Object):void{
			var facce:FindAllCatalogCategoriesEvent = new FindAllCatalogCategoriesEvent();
			facce.dispatch();
		}		
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);			
		}
	}
}