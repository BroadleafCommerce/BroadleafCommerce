package org.broadleafcommerce.admin.catalog.commands.media
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.vo.media.Media;

	public class AddMediaCommand implements Command
	{
		public function AddMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("execute : ");
			var newMedia:Media = new Media();
			CatalogModelLocator.getInstance().mediaModel.currentMedia = newMedia;	
			var x:ProductModel = CatalogModelLocator.getInstance().productModel;		
		}
		
	}
}