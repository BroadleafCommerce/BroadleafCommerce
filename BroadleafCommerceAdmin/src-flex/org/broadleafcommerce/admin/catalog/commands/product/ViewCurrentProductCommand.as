package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;

	public class ViewCurrentProductCommand implements Command
	{
		public function ViewCurrentProductCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var catalogModel:CatalogModel = CatalogModelLocator.getInstance().catalogModel;
			catalogModel.viewState = CatalogModel.STATE_VIEW_PRODUCT;
		}
		
	}
}