package org.broadleafcommerce.admin.catalog.model
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.model.IModelLocator;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.model.SkuModel;

	public class CatalogModelLocator implements IModelLocator
	{
		private static var modelLocator:CatalogModelLocator;


		public static function getInstance():CatalogModelLocator
		{
			if(modelLocator == null)
				modelLocator = new CatalogModelLocator();
			
			return modelLocator;
		}
		
		public function CatalogModelLocator()
		{
			if(modelLocator != null)
				throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "BlcAdminModelLocator");				
		}
		
		
		[Bindable]
		public var catalogTree:ArrayCollection = new ArrayCollection();
		
		[Bindable]
		public var catalogModel:CatalogModel = new CatalogModel();

		[Bindable]
		public var categoryModel:CategoryModel = new CategoryModel();
		
		[Bindable]
		public var productModel:ProductModel = new ProductModel();
		
		[Bindable]
		public var skuModel:SkuModel = new SkuModel();		
	}
}