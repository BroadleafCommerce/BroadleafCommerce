package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.view.components.MediaCanvasViewHelper;
	import org.broadleafcommerce.admin.catalog.vo.Media;

	public class AddCategoryMediaCommand implements Command
	{
		public function AddCategoryMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("execute : ");
			var newMedia:Media = new Media();
			var categoryMedia:ArrayCollection = CatalogModelLocator.getInstance().categoryModel.categoryMedia; 
			categoryMedia.addItem(newMedia);
			
			MediaCanvasViewHelper(ViewLocator.getInstance().getViewHelper("categoryMediaCanvasViewHelper")).editMedia(newMedia);
//			var sfue:ShowFileUploadEvent = new ShowFileUploadEvent("",newMedia);
//			sfue.dispatch();
//			var currentCategory:Category = CatalogModelLocator.getInstance().categoryModel.currentCategory;
//			currentCategory.categoryMedia["new"] = newMedia;
			
		}
		
	}
}