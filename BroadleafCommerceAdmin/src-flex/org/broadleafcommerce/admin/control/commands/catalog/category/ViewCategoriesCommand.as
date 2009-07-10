package org.broadleafcommerce.admin.control.commands.catalog.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.view.CatalogModel;

	public class ViewCategoriesCommand implements Command
	{
		public function ViewCategoriesCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var catalogModel:CatalogModel = AppModelLocator.getInstance().catalogModel;
			catalogModel.viewState = CatalogModel.STATE_VIEW_CATEGORY;
		}
		
	}
}