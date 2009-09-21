package org.broadleafcommerce.admin.catalog.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.StandardizeCatalogObjectsEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.AddCategoriesToCatalogTreeEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.model.SkuModel;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;

	public class BuildCatalogChainCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();
		
		public function BuildCatalogChainCommand()
		{
			var catalogModel:CatalogModel = CatalogModelLocator.getInstance().catalogModel;
			var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel;			
			var productModel:ProductModel = CatalogModelLocator.getInstance().productModel;
			var skuModel:SkuModel = CatalogModelLocator.getInstance().skuModel;



			eventChain.addItem(new StandardizeCatalogObjectsEvent(categoryModel.categoryArray, 
															      productModel.catalogProducts, 
															      skuModel.catalogSkus));
			eventChain.addItem(new AddCategoriesToCatalogTreeEvent(catalogModel.catalogTree, catalogModel.catalogTreeItemArray));		
																					  
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: BuildCatalogCommand.execute()");
			var categoriesArray:ArrayCollection = CatalogModelLocator.getInstance().categoryModel.categoryArray;

			if(categoriesArray.length > 0)
			{	
				
				var codes:ArrayCollection = AppModelLocator.getInstance().configModel.codeTypes;
				var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel;
				var productModel:ProductModel = CatalogModelLocator.getInstance().productModel;
				categoryModel.categoryMediaCodes = new ArrayCollection();
				productModel.productMediaCodes = new ArrayCollection();
				for each(var codeType:CodeType in codes){
					if(codeType.codeType == "CATEGORY_MEDIA"){
						categoryModel.categoryMediaCodes.addItem(codeType);
					}
					if(codeType.codeType == "PRODUCT_MEDIA") {
						productModel.productMediaCodes.addItem(codeType);
					}	
				}
						
				for each(var event:CairngormEvent in eventChain){
					event.dispatch();
				}
			}
		}
		
	}
}