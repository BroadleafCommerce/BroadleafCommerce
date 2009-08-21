package org.broadleafcommerce.admin.catalog.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import mx.controls.Alert;
	
	import org.broadleafcommerce.admin.catalog.control.events.SaveMediaEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryMediaEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.view.CatalogCanvasViewHelper;
	import org.broadleafcommerce.admin.catalog.vo.Media;

	public class SaveMediaCommand implements Command
	{
		public function SaveMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{	
			trace("SaveMediaCommand.execute()");
			var sme:SaveMediaEvent = SaveMediaEvent(event);
			var media:Media = sme.media;
			var currentViewState:String = CatalogCanvasViewHelper(ViewLocator.getInstance().getViewHelper("catalogCanvas")).getViewIndex();
			var categoryViewState:String = CatalogModel.STATE_VIEW_CATEGORY;
			var productViewState:String = CatalogModel.STATE_VIEW_PRODUCT;
			
			   	
			
			if(currentViewState ==  categoryViewState){
				var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel;
				categoryModel.currentCategory.categoryMedia[media.key] = media;				
				var sce:SaveCategoryEvent = new SaveCategoryEvent(CatalogModelLocator.getInstance().categoryModel.currentCategory);
				sce.dispatch();
					
			}			
			   }
		}
		
	}
