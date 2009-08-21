package org.broadleafcommerce.admin.catalog.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import org.broadleafcommerce.admin.catalog.control.events.ShowFileUploadEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.view.CatalogCanvasViewHelper;
	import org.broadleafcommerce.admin.catalog.view.components.MediaNewWindowViewHelper;
	import org.broadleafcommerce.admin.catalog.vo.Media;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class ShowFileUploadCommand implements Command
	{
		public function ShowFileUploadCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("execute : ");
			var currentViewState:String = CatalogCanvasViewHelper(ViewLocator.getInstance().getViewHelper("catalogCanvas")).getViewIndex();
			var categoryViewState:String = CatalogModel.STATE_VIEW_CATEGORY;
			var productViewState:String = CatalogModel.STATE_VIEW_PRODUCT;
			var sfue:ShowFileUploadEvent = ShowFileUploadEvent(event);
			if(currentViewState ==  categoryViewState){
				var category:Category = CatalogModelLocator.getInstance().categoryModel.currentCategory;
				//MediaCanvasViewHelper(ViewLocator.getInstance().getViewHelper("categoryMediaCanvasViewHelper")).uploadImage("/images/category/"+category.id+"/",Media(sfue.viewData));
				MediaNewWindowViewHelper(ViewLocator.getInstance().getViewHelper("mediaNewWindowViewHelper")).uploadImage("/images/category/"+category.id+"/",Media(sfue.viewData));				
			}
		}
		
	}
}