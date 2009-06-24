package org.broadleafcommerce.admin.model
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.model.IModelLocator;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.model.view.OfferModel;

	public class AppModelLocator implements IModelLocator
	{
		private static var modelLocator:AppModelLocator;

		[Bindable]
		public var offerModel:OfferModel = new OfferModel(); 

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
		public var catalogCategories:ArrayCollection = new ArrayCollection();
		[Bindable]
		public var catalogProducts:ArrayCollection = new ArrayCollection();
		[Bindable]
		public var catalogSkus:ArrayCollection = new ArrayCollection();
		
	}
}