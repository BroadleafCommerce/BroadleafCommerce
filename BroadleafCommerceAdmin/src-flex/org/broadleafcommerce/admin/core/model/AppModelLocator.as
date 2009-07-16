package org.broadleafcommerce.admin.core.model
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.model.IModelLocator;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.model.SkuModel;
	import org.broadleafcommerce.admin.offers.model.OfferModel;

	public class AppModelLocator implements IModelLocator
	{
		private static var modelLocator:AppModelLocator;


		public static function getInstance():AppModelLocator
		{
			if(modelLocator == null)
				modelLocator = new AppModelLocator();
			
			return modelLocator;
		}
		
		public function AppModelLocator()
		{
			if(modelLocator != null)
				throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "BlcAdminModelLocator");				
		}
		
		
		[Bindable]
		public var offerModel:OfferModel = new OfferModel(); 

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