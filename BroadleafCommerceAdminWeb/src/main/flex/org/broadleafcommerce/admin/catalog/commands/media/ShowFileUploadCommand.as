package org.broadleafcommerce.admin.catalog.commands.media
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import org.broadleafcommerce.admin.catalog.control.events.media.ShowFileUploadEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.view.CatalogCanvasViewHelper;
	import org.broadleafcommerce.admin.catalog.view.media.MediaNewWindowViewHelper;
	import org.broadleafcommerce.admin.catalog.view.product.ProductCanvasViewHelper;
	import org.broadleafcommerce.admin.catalog.vo.media.Media;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class ShowFileUploadCommand implements Command
	{
		public function ShowFileUploadCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("execute : ");
			var catalogModelLocator:CatalogModelLocator = CatalogModelLocator.getInstance();
			var currentViewState:String = CatalogCanvasViewHelper(ViewLocator.getInstance().getViewHelper("catalogCanvas")).getViewIndex();
			var categoryViewState:String = CatalogModel.STATE_VIEW_CATEGORY;
			var productViewState:String = CatalogModel.STATE_VIEW_PRODUCT;
			var sfue:ShowFileUploadEvent = ShowFileUploadEvent(event);
			if(currentViewState ==  categoryViewState){
				var category:Category = catalogModelLocator.categoryModel.currentCategory;
				MediaNewWindowViewHelper(ViewLocator.getInstance().getViewHelper("mediaNewWindowViewHelper")).uploadImage("/images/category/"+category.id+"/",Media(sfue.viewData));
			}
			if(currentViewState == productViewState){
				var product:Product = catalogModelLocator.productModel.currentProduct;
				MediaNewWindowViewHelper(ViewLocator.getInstance().getViewHelper("mediaNewWindowViewHelper")).uploadImage("/images/product/"+product.id+"/",Media(sfue.viewData));			}
		}
		
	}
}