package org.broadleafcommerce.admin.catalog.commands.media
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.media.SaveMediaEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.view.CatalogCanvasViewHelper;
	import org.broadleafcommerce.admin.catalog.vo.media.Media;

	public class SaveMediaCommand implements Command
	{
		public function SaveMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{	
			trace("DEBUG: SaveMediaCommand.execute()");
			var sme:SaveMediaEvent = SaveMediaEvent(event);
			var media:Media = sme.media;
			var catalogModelLocator:CatalogModelLocator = CatalogModelLocator.getInstance();
			var currentViewState:String = CatalogCanvasViewHelper(ViewLocator.getInstance().getViewHelper("catalogCanvas")).getViewIndex();
			var categoryViewState:String = CatalogModel.STATE_VIEW_CATEGORY;
			var productViewState:String = CatalogModel.STATE_VIEW_PRODUCT;
			
			   	
			
			if(currentViewState ==  categoryViewState){
				var categoryModel:CategoryModel = catalogModelLocator.categoryModel;
				categoryModel.currentCategory.categoryMedia[media.key] = media;
				var isNewMedia:Boolean = true;
				for each(var catMedia:Media in categoryModel.categoryMedia){
					if(catMedia.id == media.id){
						isNewMedia = false;
					}
				}
				if(isNewMedia){
					categoryModel.categoryMedia.addItem(media);					
				}
				var sce:SaveCategoryEvent = new SaveCategoryEvent(catalogModelLocator.categoryModel.currentCategory);
				sce.dispatch();
					
			}
			if(currentViewState == productViewState){
				var productModel:ProductModel = catalogModelLocator.productModel;
				productModel.currentProduct.productMedia[media.key] = media;
				var spe:SaveProductEvent = new SaveProductEvent(catalogModelLocator.productModel.currentProduct);
				spe.dispatch();
			}			
		}
	}
		
}
