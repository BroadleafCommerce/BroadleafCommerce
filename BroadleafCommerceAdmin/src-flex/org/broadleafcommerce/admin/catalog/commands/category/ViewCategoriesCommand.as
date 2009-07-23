package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;

	public class ViewCategoriesCommand implements Command
	{
		public function ViewCategoriesCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var catalogModel:CatalogModel = CatalogModelLocator.getInstance().catalogModel;
			catalogModel.viewState = CatalogModel.STATE_VIEW_CATEGORY;
		}
		
	}
}