package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.view.product.ProductCanvasViewHelper;
	import org.broadleafcommerce.admin.catalog.vo.media.Media;

	public class AddProductMediaCommand implements Command
	{
		public function AddProductMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("execute : ");
			var newMedia:Media = new Media();
			var productMedia:ArrayCollection = CatalogModelLocator.getInstance().productModel.productMedia;
			productMedia.addItem(newMedia);
			
			ProductCanvasViewHelper(ViewLocator.getInstance().getViewHelper("productCanvasViewHelper")).editMedia(newMedia);
			
		}
		
	}
}